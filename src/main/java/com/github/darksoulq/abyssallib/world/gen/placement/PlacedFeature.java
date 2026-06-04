package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.feature.ConfiguredFeature;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenLoader;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a feature that is fully prepared for world placement.
 * <p>
 * A PlacedFeature wraps a {@link ConfiguredFeature} with an ordered list of
 * {@link PlacementModifier}s. It acts as the execution pipeline, translating
 * initial coordinates through the modifiers before delegating to the feature logic.
 *
 * @param feature   The configured feature to generate.
 * @param placement The ordered list of modifiers to apply to the initial position.
 */
public record PlacedFeature(ConfiguredFeature<?, ?> feature, List<PlacementModifier> placement) {

    /**
     * The internal codec for decoding a PlacedFeature defined explicitly as a JSON object.
     */
    private static final Codec<PlacedFeature> INLINE_CODEC = Codec.of(
        new Codec.Decoder<PlacedFeature>() {
            @Override
            public <D> DataResult<PlacedFeature> decode(DynamicOps<D> ops, D input) {
                return ops.getMap(input)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error("Expected map for PlacedFeature"))
                    .flatMap(map -> {
                        D typeNode = map.get(ops.createString("type"));
                        if (typeNode == null) return DataResult.error("Missing 'type' in PlacedFeature");

                        return Codecs.STRING.decode(ops, typeNode).flatMap(typeId -> {
                            Feature<?> featureBase = Registries.FEATURES.get(typeId);
                            if (featureBase == null) return DataResult.error("Unknown feature type: " + typeId);

                            D configNode = map.get(ops.createString("config"));
                            if (configNode == null) return DataResult.error("Missing 'config' in PlacedFeature");

                            return featureBase.getCodec().decode(ops, configNode).flatMap(config -> {
                                List<PlacementModifier> modifiers = new ArrayList<>();
                                D placementNode = map.get(ops.createString("placement"));
                                if (placementNode != null) {
                                    return PlacementModifier.CODEC.list().decode(ops, placementNode).map(mods -> createConfigured(featureBase, config, mods));
                                }
                                return DataResult.success(createConfigured(featureBase, config, modifiers));
                            });
                        });
                    });
            }
        },
        new Codec.Encoder<PlacedFeature>() {
            @Override
            @SuppressWarnings("unchecked")
            public <D> DataResult<D> encode(DynamicOps<D> ops, PlacedFeature value) {
                Map<D, D> map = new HashMap<>();

                Feature<FeatureConfig> featureBase = (Feature<FeatureConfig>) value.feature().feature();
                String typeId = Registries.FEATURES.getId(featureBase);
                if (typeId == null) return DataResult.error("Unregistered feature type");

                DataResult<D> typeRes = Codecs.STRING.encode(ops, typeId);
                if (typeRes.isError()) return typeRes;
                map.put(ops.createString("type"), typeRes.getOrThrow());

                DataResult<D> configRes = featureBase.getCodec().encode(ops, value.feature().config());
                if (configRes.isError()) return configRes;
                map.put(ops.createString("config"), configRes.getOrThrow());

                if (!value.placement().isEmpty()) {
                    DataResult<D> placementRes = PlacementModifier.CODEC.list().encode(ops, value.placement());
                    if (placementRes.isError()) return placementRes;
                    map.put(ops.createString("placement"), placementRes.getOrThrow());
                }

                return DataResult.success(ops.createMap(map));
            }
        }
    ).describe("InlinePlacedFeature");

    /**
     * The internal codec for decoding a PlacedFeature defined as a string reference ID.
     */
    private static final Codec<PlacedFeature> REFERENCE_CODEC = Codecs.STRING.xmap(
        WorldGenLoader::resolveReference,
        feature -> "inline_feature"
    ).describe("ReferencePlacedFeature");

    /**
     * The universal codec for PlacedFeatures, supporting both direct object definitions
     * and string-based registry lookups.
     */
    public static final Codec<PlacedFeature> CODEC = Codec.fallback(INLINE_CODEC, REFERENCE_CODEC).describe("PlacedFeature");

    /**
     * Helper method to safely bind wildcards when instantiating the ConfiguredFeature.
     *
     * @param feature   The raw feature.
     * @param config    The raw configuration.
     * @param placement The modifier list.
     * @param <C>       The configuration type parameter.
     * @return The properly casted PlacedFeature.
     */
    @SuppressWarnings("unchecked")
    private static <C extends FeatureConfig> PlacedFeature createConfigured(Feature<?> feature, FeatureConfig config, List<PlacementModifier> placement) {
        return new PlacedFeature(new ConfiguredFeature<>((Feature<C>) feature, (C) config), placement);
    }

    /**
     * Triggers the placement pipeline starting from a chunk's root coordinates.
     *
     * @param level  The world generation accessor.
     * @param random The random source for this chunk.
     * @param chunkX The chunk X coordinate.
     * @param chunkZ The chunk Z coordinate.
     * @return True if the feature was successfully placed at least once.
     */
    public boolean place(WorldGenAccess level, Random random, int chunkX, int chunkZ) {
        int startX = (chunkX << 4) + 8;
        int startZ = (chunkZ << 4) + 8;
        return place(level, random, new Location(level.getWorld(), startX, 0, startZ));
    }

    /**
     * Triggers the placement pipeline starting from an absolute origin location.
     * <p>
     * This is strictly utilized by Meta Features (like RandomFeature) to execute
     * sub-features relative to the currently evaluated position in the world.
     *
     * @param level  The world generation accessor.
     * @param random The random source.
     * @param origin The absolute starting coordinate for the modifier pipeline.
     * @return True if the feature was successfully placed at least once.
     */
    public boolean place(WorldGenAccess level, Random random, Location origin) {
        Stream<Vector> positions = Stream.of(origin.toVector());
        PlacementContext context = new PlacementContext(level, origin.getBlockX() >> 4, origin.getBlockZ() >> 4, random);

        for (PlacementModifier modifier : placement) {
            positions = modifier.getPositions(context, positions);
        }

        boolean success = false;
        for (Vector pos : positions.toList()) {
            if (feature.place(level, context.toLocation(pos), random)) {
                success = true;
            }
        }
        return success;
    }
}