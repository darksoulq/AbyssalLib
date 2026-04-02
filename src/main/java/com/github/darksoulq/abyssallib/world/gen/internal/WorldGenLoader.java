package com.github.darksoulq.abyssallib.world.gen.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacedFeature;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Utility class responsible for the two-pass loading, parsing, and registration
 * of world generation features from JSON files.
 * <p>
 * Pass 1 indexes all raw JSON nodes against their file paths.
 * Pass 2 decodes the nodes, resolving string references on-demand to support
 * deeply nested Meta Features without strict file ordering requirements.
 */
public class WorldGenLoader {

    /** The Jackson mapper for JSON tree parsing. */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** The target directory for custom worldgen feature files. */
    private static final File FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "worldgen/features");

    /** Cache of unparsed JSON files discovered during Pass 1. */
    private static final Map<String, JsonNode> RAW_NODES = new HashMap<>();

    /** Cache of fully decoded feature instances resolved during Pass 2. */
    private static final Map<String, PlacedFeature> RESOLVED_FEATURES = new HashMap<>();

    /**
     * Initializes the two-pass loading process.
     */
    public static void load() {
        RAW_NODES.clear();
        RESOLVED_FEATURES.clear();

        if (!FOLDER.exists()) {
            FOLDER.mkdirs();
            return;
        }

        try (Stream<Path> paths = Files.walk(FOLDER.toPath())) {
            paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(WorldGenLoader::cacheRawNode);
        } catch (IOException e) {
            AbyssalLib.LOGGER.severe("Failed to read worldgen directory: " + e.getMessage());
            return;
        }

        for (String id : RAW_NODES.keySet()) {
            try {
                PlacedFeature feature = resolveReference(id);
                if (feature == null) continue;

                JsonNode root = RAW_NODES.get(id);

                if (root.has("worlds")) {
                    List<String> worlds = Codecs.STRING.list().decode(JsonOps.INSTANCE, root.get("worlds"));
                    for (String worldName : worlds) {
                        WorldGenManager.addFeature(worldName, feature);
                    }
                }
            } catch (Exception e) {
                AbyssalLib.LOGGER.warning("Failed to initialize feature '" + id + "': " + e.getMessage() + ". Skipping loading for this file.");
            }
        }
    }

    /**
     * Loads a standalone feature from a specific file path on the disk without
     * registering it into the global cache.
     *
     * @param path The path to the file.
     * @return The deserialized {@link PlacedFeature}, or {@code null} if loading fails.
     */
    public static PlacedFeature load(Path path) {
        return Try.of(() -> {
            JsonNode root = MAPPER.readTree(path.toFile());
            return PlacedFeature.CODEC.decode(JsonOps.INSTANCE, root);
        }).onFailure(e -> AbyssalLib.LOGGER.warning("Failed to load standalone feature from " + path + ": " + e.getMessage() + ". Skipping.")).orElse(null);
    }

    /**
     * Loads a feature embedded within a plugin's JAR resources without registering it.
     *
     * @param plugin       The plugin owning the resource.
     * @param resourcePath The internal path within the JAR.
     * @return The deserialized {@link PlacedFeature}, or {@code null} if not found or invalid.
     */
    public static PlacedFeature loadResource(Plugin plugin, String resourcePath) {
        return Try.of(() -> {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in == null) {
                    AbyssalLib.LOGGER.warning("Feature resource not found at " + resourcePath + ". Skipping.");
                    return null;
                }
                JsonNode root = MAPPER.readTree(in);
                return PlacedFeature.CODEC.decode(JsonOps.INSTANCE, root);
            }
        }).onFailure(e -> AbyssalLib.LOGGER.warning("Failed to load feature resource from " + resourcePath + ": " + e.getMessage() + ". Skipping.")).orElse(null);
    }

    /**
     * Caches a discovered JSON file into the raw node map for later resolution.
     *
     * @param path The file system path to the JSON file.
     */
    private static void cacheRawNode(Path path) {
        try {
            Key id = getFeatureId(path);
            if (id != null) {
                RAW_NODES.put(id.asString(), MAPPER.readTree(path.toFile()));
            }
        } catch (Exception e) {
            AbyssalLib.LOGGER.warning("Failed to parse JSON syntax in file " + path + ": " + e.getMessage() + ". Skipping.");
        }
    }

    /**
     * Resolves a string identifier into a fully instantiated PlacedFeature.
     * <p>
     * If the feature is already resolved, it is returned from the cache. If not,
     * it decodes the associated raw JSON node immediately. This recursion safely
     * handles deeply nested feature definitions.
     *
     * @param id The namespaced identifier of the requested feature.
     * @return The fully constructed PlacedFeature, or null if it cannot be resolved.
     */
    public static PlacedFeature resolveReference(String id) {
        if (RESOLVED_FEATURES.containsKey(id)) {
            return RESOLVED_FEATURES.get(id);
        }

        if (!RAW_NODES.containsKey(id)) {
            AbyssalLib.LOGGER.warning("Unknown feature reference requested: '" + id + "'. Skipping.");
            return null;
        }

        JsonNode node = RAW_NODES.get(id);

        try {
            PlacedFeature feature = PlacedFeature.CODEC.decode(JsonOps.INSTANCE, node);
            RESOLVED_FEATURES.put(id, feature);
            return feature;
        } catch (Exception e) {
            AbyssalLib.LOGGER.warning("Failed to decode referenced feature '" + id + "': " + e.getMessage() + ". Skipping.");
            return null;
        }
    }

    /**
     * Computes the namespaced ID of a feature based on its relative folder path.
     *
     * @param file The absolute path to the file.
     * @return The computed namespace key, or null if the path is invalid.
     */
    private static Key getFeatureId(Path file) {
        Path relative = FOLDER.toPath().relativize(file);
        if (relative.getNameCount() < 2) {
            AbyssalLib.LOGGER.warning("Skipping feature file " + file + ": Must be inside a namespace folder.");
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
        return Key.key(namespace, fullPath);
    }
}