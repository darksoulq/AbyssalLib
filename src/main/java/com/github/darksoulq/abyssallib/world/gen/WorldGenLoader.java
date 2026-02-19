package com.github.darksoulq.abyssallib.world.gen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.feature.ConfiguredFeature;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenManager;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacedFeature;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class responsible for loading, parsing, and registering world generation features from JSON files.
 * This loader follows the standard Minecraft worldgen hierarchy:
 * <ul>
 * <li><b>Feature:</b> The base generation logic (e.g., Ore, Tree, Lake).</li>
 * <li><b>ConfiguredFeature:</b> A Feature paired with specific parameters (e.g., Iron Ore with size 9).</li>
 * <li><b>PlacedFeature:</b> A ConfiguredFeature paired with {@link PlacementModifier}s (e.g., 20 attempts per chunk).</li>
 * </ul>
 */
public class WorldGenLoader {
    /** The Jackson mapper for JSON tree parsing. */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** The target directory for custom worldgen feature files. */
    private static final File FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "worldgen/features");

    /**
     * Initializes the loading process by scanning the {@code worldgen/features} directory.
     * <p>
     * Valid {@code .json} files are parsed and their features are registered to the
     * {@link WorldGenManager} for the specified worlds.
     * </p>
     */
    public static void load() {
        if (!FOLDER.exists()) {
            FOLDER.mkdirs();
            return;
        }

        try (Stream<Path> paths = Files.walk(FOLDER.toPath())) {
            paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(WorldGenLoader::loadFileAndRegister);
        } catch (IOException e) {
            AbyssalLib.LOGGER.severe("Failed to load worldgen features: " + e.getMessage());
        }
    }

    /**
     * Internal helper to load a specific file and register its contents to the target worlds.
     *
     * @param path The {@link Path} to the JSON configuration file.
     */
    private static void loadFileAndRegister(Path path) {
        try {
            JsonNode root = MAPPER.readTree(path.toFile());
            PlacedFeature placedFeature = loadFromNode(root);

            if (placedFeature != null && root.has("worlds")) {
                List<String> worlds = Codecs.STRING.list().decode(JsonOps.INSTANCE, root.get("worlds"));
                for (String worldName : worlds) {
                    WorldGenManager.addFeature(worldName, placedFeature);
                }
            }
        } catch (Exception e) {
            AbyssalLib.LOGGER.warning("Failed to parse feature file " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Decodes a {@link PlacedFeature} from a file on disk.
     *
     * @param path The file system path.
     * @return The resulting {@link PlacedFeature}, or {@code null} if loading fails.
     */
    public static PlacedFeature load(Path path) {
        try {
            JsonNode root = MAPPER.readTree(path.toFile());
            return loadFromNode(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decodes a {@link PlacedFeature} from a plugin's internal resource folder.
     *
     * @param plugin       The plugin providing the resource.
     * @param resourcePath The internal path (e.g., "features/my_feature.json").
     * @return The resulting {@link PlacedFeature}, or {@code null} if not found.
     */
    public static PlacedFeature loadResource(Plugin plugin, String resourcePath) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) return null;
            JsonNode root = MAPPER.readTree(in);
            return loadFromNode(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Core parsing logic that converts a {@link JsonNode} into a {@link PlacedFeature}.
     * <p>
     * This method resolves the feature type from the {@link Registries#FEATURES},
     * decodes the {@link FeatureConfig}, and builds the {@link ConfiguredFeature}
     * before applying the list of {@link PlacementModifier}s.
     * </p>
     *
     * @param root The root JSON object.
     * @return A fully constructed {@link PlacedFeature}.
     * @throws Exception If the configuration is invalid or the feature type is unknown.
     */
    private static PlacedFeature loadFromNode(JsonNode root) throws Exception {
        String featureTypeId = root.get("type").asText();
        Feature<?> feature = Registries.FEATURES.get(featureTypeId);
        if (feature == null) {
            return null;
        }

        FeatureConfig config = feature.getCodec().decode(JsonOps.INSTANCE, root.get("config"));
        ConfiguredFeature<?, ?> configured = new ConfiguredFeature(feature, config);

        List<PlacementModifier> placement = new ArrayList<>();
        if (root.has("placement")) {
            placement = PlacementModifier.CODEC.list().decode(JsonOps.INSTANCE, root.get("placement"));
        }

        return new PlacedFeature(configured, placement);
    }
}