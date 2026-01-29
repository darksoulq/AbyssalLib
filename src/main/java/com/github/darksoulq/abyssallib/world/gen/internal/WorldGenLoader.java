package com.github.darksoulq.abyssallib.world.gen.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.feature.ConfiguredFeature;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
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

public class WorldGenLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final File FOLDER = new File(AbyssalLib.getInstance().getDataFolder(), "worldgen/features");

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

    public static PlacedFeature load(Path path) {
        try {
            JsonNode root = MAPPER.readTree(path.toFile());
            return loadFromNode(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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