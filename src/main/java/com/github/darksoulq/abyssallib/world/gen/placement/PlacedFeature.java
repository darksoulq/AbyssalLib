package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
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
    private static final Codec<PlacedFeature> INLINE_CODEC = new Codec<>() {

        /**
         * Decodes an inline feature definition from a map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return The decoded placed feature.
         * @throws CodecException If required fields or the feature type are missing.
         */
        @Override
        public <D> PlacedFeature decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for PlacedFeature"));

            String typeId = Codecs.STRING.decode(ops, map.get(ops.createString("type")));
            Feature<?> featureBase = Registries.FEATURES.get(typeId);
            if (featureBase == null) {
                throw new CodecException("Unknown feature type: " + typeId);
            }

            FeatureConfig config = featureBase.getCodec().decode(ops, map.get(ops.createString("config")));

            List<PlacementModifier> modifiers = new ArrayList<>();
            D placementNode = map.get(ops.createString("placement"));
            if (placementNode != null) {
                modifiers = PlacementModifier.CODEC.list().decode(ops, placementNode);
            }

            return createConfigured(featureBase, config, modifiers);
        }

        /**
         * Encodes a placed feature into an inline map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The feature to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, PlacedFeature value) throws CodecException {
            Map<D, D> map = new HashMap<>();

            Feature<FeatureConfig> featureBase = (Feature<FeatureConfig>) value.feature().feature();
            String typeId = Registries.FEATURES.getId(featureBase);

            map.put(ops.createString("type"), Codecs.STRING.encode(ops, typeId));
            map.put(ops.createString("config"), featureBase.getCodec().encode(ops, value.feature().config()));

            if (!value.placement().isEmpty()) {
                map.put(ops.createString("placement"), PlacementModifier.CODEC.list().encode(ops, value.placement()));
            }

            return ops.createMap(map);
        }
    };

    /**
     * The internal codec for decoding a PlacedFeature defined as a string reference ID.
     */
    private static final Codec<PlacedFeature> REFERENCE_CODEC = Codecs.STRING.xmap(
        WorldGenLoader::resolveReference,
        feature -> "inline_feature"
    );

    /**
     * The universal codec for PlacedFeatures, supporting both direct object definitions
     * and string-based registry lookups.
     */
    public static final Codec<PlacedFeature> CODEC = Codec.fallback(INLINE_CODEC, REFERENCE_CODEC);

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