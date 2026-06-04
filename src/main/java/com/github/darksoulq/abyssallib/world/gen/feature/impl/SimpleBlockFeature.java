package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;

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
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Config.class, Config::stateProvider)
        ).apply(instance, Config::new)).describe("SimpleBlockConfig");
    }
}