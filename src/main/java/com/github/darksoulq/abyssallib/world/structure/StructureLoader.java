package com.github.darksoulq.abyssallib.world.structure;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Manages the loading, saving, and registration of serialized structure files.
 * This class scans the {@code structures/} directory and automatically registers
 * valid JSON and compressed structural files into the {@link Registries#STRUCTURES} registry.
 * It relies on namespaced subfolders to determine the unique {@link Key} for each structure.
 */
public class StructureLoader {

    /**
     * The root directory where structure files are stored on the server file system.
     */
    private static final Path STRUCTURES_FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "structures").toPath();

    /**
     * Initializes the structure folder and performs a recursive scan for valid files.
     * Found files ending in {@code .struct} or {@code .json} are processed and automatically
     * registered into the internal structure registry.
     */
    public static void load() {
        if (!Files.exists(STRUCTURES_FOLDER)) {
            try {
                Files.createDirectories(STRUCTURES_FOLDER);
            } catch (IOException e) {
                AbyssalLib.LOGGER.severe("Failed to create structures folder: " + e.getMessage());
                return;
            }
        }

        try (Stream<Path> stream = Files.walk(STRUCTURES_FOLDER)) {
            stream.filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".struct") || path.toString().endsWith(".json"))
                .forEach(StructureLoader::loadFileAndRegister);
        } catch (IOException e) {
            AbyssalLib.LOGGER.severe("Failed to walk structures folder: " + e.getMessage());
        }
    }

    /**
     * Internal helper to derive an identifier from a file path and register the structure.
     *
     * @param path The {@link Path} to the serialized structure file on the disk.
     */
    private static void loadFileAndRegister(Path path) {
        Key id = getStructureId(path);
        if (id == null) {
            return;
        }

        Structure structure = load(path);
        if (structure != null) {
            if (Registries.STRUCTURES.contains(id.asString())) {
                AbyssalLib.LOGGER.warning("Duplicate structure ID '" + id + "' found in file " + path + ". Skipping registration.");
            } else {
                Registries.STRUCTURES.register(id.asString(), structure);
            }
        }
    }

    /**
     * Loads and deserializes a structure from a specific file path on the disk.
     * Supports both raw JSON files and GZIP compressed {@code .struct} files.
     *
     * @param path The {@link Path} pointing to the target file.
     * @return The deserialized {@link Structure} instance, or null if parsing fails.
     */
    public static Structure load(Path path) {
        return Try.of(() -> {
            JsonNode root;
            if (path.toString().endsWith(".struct")) {
                try (GZIPInputStream gis = new GZIPInputStream(Files.newInputStream(path))) {
                    root = JsonOps.INSTANCE.mapper.readTree(gis);
                }
            } else {
                root = JsonOps.INSTANCE.mapper.readTree(path.toFile());
            }

            DataResult<Structure> res = Structure.CODEC.decode(JsonOps.INSTANCE, root);
            if (res.isError()) {
                AbyssalLib.LOGGER.severe("Failed to decode structure from " + path + ": " + res.error().get());
                return null;
            }
            if (res.isPartial()) {
                res.warnings().forEach(warning -> AbyssalLib.LOGGER.warning("Structure decoding warning: " + warning.message()));
            }
            return res.getOrThrow();

        }).onFailure(e -> AbyssalLib.LOGGER.warning("Failed to load structure from " + path + ": " + e.getMessage())).orElse(null);
    }

    /**
     * Loads a structure embedded within a plugin's internal JAR resources.
     * Supports both raw JSON files and GZIP compressed {@code .struct} files.
     *
     * @param plugin       The {@link Plugin} instance owning the internal resource.
     * @param resourcePath The internal String path within the JAR (e.g., "assets/myplugin/structures/house.json").
     * @return The deserialized {@link Structure} instance, or null if the resource is missing or invalid.
     */
    public static Structure loadResource(Plugin plugin, String resourcePath) {
        return Try.of(() -> {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in == null) {
                    return null;
                }
                JsonNode root;
                if (resourcePath.endsWith(".struct")) {
                    try (GZIPInputStream gis = new GZIPInputStream(in)) {
                        root = JsonOps.INSTANCE.mapper.readTree(gis);
                    }
                } else {
                    root = JsonOps.INSTANCE.mapper.readTree(in);
                }

                DataResult<Structure> res = Structure.CODEC.decode(JsonOps.INSTANCE, root);
                if (res.isError()) {
                    AbyssalLib.LOGGER.severe("Failed to decode structure from resource " + resourcePath + ": " + res.error().get());
                    return null;
                }
                if (res.isPartial()) {
                    res.warnings().forEach(warning -> AbyssalLib.LOGGER.warning("Structure decoding warning: " + warning.message()));
                }
                return res.getOrThrow();
            }
        }).onFailure(Throwable::printStackTrace).orElse(null);
    }

    /**
     * Serializes and saves a structure to the disk using the specified identifier.
     * The file is automatically saved at {@code structures/<namespace>/<path>.struct}
     * utilizing GZIP compression to minimize disk footprint.
     *
     * @param id        The {@link Key} defining the namespace and file name layout.
     * @param structure The {@link Structure} instance to serialize and save.
     * @return True if the I/O write operation was successful, false otherwise.
     */
    public static boolean save(Key id, Structure structure) {
        Path namespaceFolder = STRUCTURES_FOLDER.resolve(id.namespace());
        try {
            if (!Files.exists(namespaceFolder)) {
                Files.createDirectories(namespaceFolder);
            }

            Path file = namespaceFolder.resolve(id.value() + ".struct");

            DataResult<JsonNode> res = Structure.CODEC.encode(JsonOps.INSTANCE, structure);
            if (res.isError()) {
                AbyssalLib.LOGGER.severe("Failed to encode structure " + id + ": " + res.error().get());
                return false;
            }
            if (res.isPartial()) {
                res.warnings().forEach(warning -> AbyssalLib.LOGGER.warning("Structure encoding warning: " + warning.message()));
            }

            try (OutputStream os = new GZIPOutputStream(Files.newOutputStream(file))) {
                JsonOps.INSTANCE.mapper.writer().writeValue(os, res.getOrThrow());
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Determines the structure's namespaced identifier based on its relative path
     * within the root structures folder. For example, a file located at
     * {@code structures/abyssallib/dungeon/room1.json} yields the ID {@code abyssallib:dungeon/room1}.
     *
     * @param file The absolute {@link Path} to the file being evaluated.
     * @return The derived {@link Key}, or null if the file resides outside a valid namespace subfolder.
     */
    private static Key getStructureId(Path file) {
        Path relative = STRUCTURES_FOLDER.relativize(file);
        if (relative.getNameCount() < 2) {
            AbyssalLib.LOGGER.warning("Skipping structure file " + file + ": Must be inside a namespace folder (structures/<namespace>/<name>.struct)");
            return null;
        }

        String namespace = relative.getName(0).toString();
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < relative.getNameCount(); i++) {
            if (i > 1) {
                pathBuilder.append("/");
            }
            pathBuilder.append(relative.getName(i));
        }

        String fullPath = pathBuilder.toString();
        int lastDot = fullPath.lastIndexOf('.');
        if (lastDot > 0) {
            fullPath = fullPath.substring(0, lastDot);
        }
        return Key.key(namespace, fullPath);
    }
}