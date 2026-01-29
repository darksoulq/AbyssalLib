package com.github.darksoulq.abyssallib.world.structure.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.structure.Structure;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class StructureLoader {
    private static final Path STRUCTURES_FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "structures").toPath();
    private static final ObjectMapper MAPPER = new ObjectMapper();

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

    public static Structure load(Path path) {
        return Try.of(() -> {
            JsonNode root = MAPPER.readTree(path.toFile());
            return Structure.deserialize(root);
        }).onFailure(e -> AbyssalLib.LOGGER.warning("Failed to load structure from " + path + ": " + e.getMessage())).orElse(null);
    }

    public static Structure loadResource(Plugin plugin, String resourcePath) {
        return Try.of(() -> {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in == null) return null;
                JsonNode root = MAPPER.readTree(in);
                return Structure.deserialize(root);
            }
        }).onFailure(e -> e.printStackTrace()).orElse(null);
    }

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