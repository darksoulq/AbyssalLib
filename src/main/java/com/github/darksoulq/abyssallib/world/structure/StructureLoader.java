package com.github.darksoulq.abyssallib.world.structure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Manages the loading, saving, and registration of structure files.
 * <p>
 * This class scans the {@code structures/} directory and automatically registers
 * valid JSON files into the {@link Registries#STRUCTURES} registry. It uses
 * namespaced subfolders to determine the {@link Identifier} for each structure.
 */
public class StructureLoader {
    /** The root directory where structure JSON files are stored. */
    private static final Path STRUCTURES_FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "structures").toPath();
    /** Jackson ObjectMapper for JSON processing. */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Initializes the structure folder and performs a recursive scan for JSON files.
     * <p>
     * Found files are processed through {@link #loadFileAndRegister(Path)}.
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
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(StructureLoader::loadFileAndRegister);
        } catch (IOException e) {
            AbyssalLib.LOGGER.severe("Failed to walk structures folder: " + e.getMessage());
        }
    }

    /**
     * Internal helper to derive an ID from a file path and register the structure.
     *
     * @param path The path to the JSON structure file.
     */
    private static void loadFileAndRegister(Path path) {
        Identifier id = getStructureId(path);
        if (id == null) return;

        Structure structure = load(path);
        if (structure != null) {
            if (Registries.STRUCTURES.contains(id.toString())) {
                AbyssalLib.LOGGER.warning("Duplicate structure ID '" + id + "' found in file " + path + ". Skipping registration.");
            } else {
                Registries.STRUCTURES.register(id.toString(), structure);
            }
        }
    }

    /**
     * Loads a structure from a specific file path on the disk.
     *
     * @param path The path to the file.
     * @return The deserialized {@link Structure}, or {@code null} if loading fails.
     */
    public static Structure load(Path path) {
        return Try.of(() -> {
            JsonNode root = MAPPER.readTree(path.toFile());
            return Structure.deserialize(root);
        }).onFailure(e -> AbyssalLib.LOGGER.warning("Failed to load structure from " + path + ": " + e.getMessage())).orElse(null);
    }

    /**
     * Loads a structure embedded within a plugin's JAR resources.
     *
     * @param plugin       The plugin owning the resource.
     * @param resourcePath The internal path within the JAR (e.g., "assets/myplugin/structures/house.json").
     * @return The deserialized {@link Structure}, or {@code null} if not found or invalid.
     */
    public static Structure loadResource(Plugin plugin, String resourcePath) {
        return Try.of(() -> {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in == null) return null;
                JsonNode root = MAPPER.readTree(in);
                return Structure.deserialize(root);
            }
        }).onFailure(Throwable::printStackTrace).orElse(null);
    }

    /**
     * Serializes and saves a structure to the disk using the specified identifier.
     * <p>
     * The file will be saved at {@code structures/<namespace>/<path>.json}.
     *
     * @param id        The {@link Identifier} defining the namespace and file name.
     * @param structure The {@link Structure} instance to save.
     * @return {@code true} if the save was successful; {@code false} otherwise.
     */
    public static boolean save(Identifier id, Structure structure) {
        Path namespaceFolder = STRUCTURES_FOLDER.resolve(id.getNamespace());
        try {
            if (!Files.exists(namespaceFolder)) Files.createDirectories(namespaceFolder);

            Path file = namespaceFolder.resolve(id.getPath() + ".json");
            ObjectNode root = structure.serialize();
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), root);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Determines the structure's namespaced ID based on its relative path within the root folder.
     * <p>
     * For a file at {@code structures/abyssallib/dungeon/room1.json}, the resulting
     * ID will be {@code abyssallib:dungeon/room1}.
     *
     * @param file The absolute path to the file.
     * @return An {@link Identifier}, or {@code null} if the file is not in a valid subfolder.
     */
    private static Identifier getStructureId(Path file) {
        Path relative = STRUCTURES_FOLDER.relativize(file);
        if (relative.getNameCount() < 2) {
            AbyssalLib.LOGGER.warning("Skipping structure file " + file + ": Must be inside a namespace folder (structures/<namespace>/<name>.json)");
            return null;
        }

        String namespace = relative.getName(0).toString();
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < relative.getNameCount(); i++) {
            if (i > 1) pathBuilder.append("/");
            pathBuilder.append(relative.getName(i).toString());
        }

        String fullPath = pathBuilder.toString();
        int lastDot = fullPath.lastIndexOf('.');
        if (lastDot > 0) {
            fullPath = fullPath.substring(0, lastDot);
        }
        return Identifier.of(namespace, fullPath);
    }
}