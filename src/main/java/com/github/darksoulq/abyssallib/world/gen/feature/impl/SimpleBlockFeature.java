package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * A fundamental world generation feature that places a single block state dynamically.
 */
public class SimpleBlockFeature extends Feature<SimpleBlockFeature.Config> {

    /**
     * Constructs a new SimpleBlockFeature with its associated configuration codec.
     */
    public SimpleBlockFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement of the single block at the origin coordinate.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if the block was successfully placed.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        BlockInfo stateToPlace = context.config().stateProvider().getState(context.random(), context.origin());
        if (stateToPlace != null) {
            WorldGenUtils.placeBlock(context.level(), context.origin(), stateToPlace);
            return true;
        }
        return false;
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @return The VEGETAL_DECORATION generation phase.
     */
    @Override
    public GenerationPhase getPhase(Config config) {
        return GenerationPhase.VEGETAL_DECORATION;
    }

    /**
     * Configuration record for the simple block feature.
     *
     * @param stateProvider The dynamic provider supplying the block to be placed.
     */
    public record Config(BlockStateProvider stateProvider) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            /**
             * Decodes the configuration from a map.
             *
             * @param ops   The dynamic operations logic.
             * @param input The serialized input.
             * @param <D>   The data format type.
             * @return A new configuration instance.
             * @throws CodecException If the state provider field is missing.
             */
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                BlockStateProvider stateProvider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("state_provider")));
                return new Config(stateProvider);
            }

            /**
             * Encodes the configuration into a map.
             *
             * @param ops   The dynamic operations logic.
             * @param value The configuration instance.
             * @param <D>   The data format type.
             * @return The encoded data object.
             * @throws CodecException If serialization fails.
             */
            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("state_provider"), BlockStateProvider.CODEC.encode(ops, value.stateProvider));
                return ops.createMap(map);
            }
        };
    }
}