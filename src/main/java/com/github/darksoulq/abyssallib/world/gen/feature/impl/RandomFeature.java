package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacedFeature;

import java.util.List;

/**
 * A meta-feature that evaluates a list of potential sub-features sequentially,
 * placing the first one that passes its individual probability check.
 */
public class RandomFeature extends Feature<RandomFeature.Config> {

    /**
     * Constructs a new RandomFeature with its associated configuration codec.
     */
    public RandomFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the sequential probability checks and delegates placement to the selected feature.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if the selected feature successfully placed blocks.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();

        for (WeightedFeature weightedFeature : config.features()) {
            if (context.random().nextFloat() < weightedFeature.chance()) {
                return weightedFeature.feature().place(context.level(), context.random(), context.origin());
            }
        }

        return config.defaultFeature().place(context.level(), context.random(), context.origin());
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @param config The configuration containing the sub-features.
     * @return The dynamic generation phase of the underlying fallback feature.
     */
    @SuppressWarnings("unchecked")
    public GenerationPhase getPhase(Config config) {
        PlacedFeature defaultFeature = config.defaultFeature();
        if (defaultFeature != null) {
            Feature<FeatureConfig> rawFeature = (Feature<FeatureConfig>) defaultFeature.feature().feature();
            return rawFeature.getPhase(defaultFeature.feature().config());
        }
        if (config.features().isEmpty()) {
            return GenerationPhase.LOCAL_MODIFICATIONS;
        }
        PlacedFeature first = config.features().get(0).feature();
        Feature<FeatureConfig> rawFeature = (Feature<FeatureConfig>) first.feature().feature();
        return rawFeature.getPhase(first.feature().config());
    }

    /**
     * A record defining a PlacedFeature paired with an execution probability.
     *
     * @param feature The sub-feature to attempt to place.
     * @param chance  The float probability of execution.
     */
    public record WeightedFeature(PlacedFeature feature, float chance) {

        /**
         * The codec for serializing and deserializing a weighted feature.
         */
        public static final Codec<WeightedFeature> CODEC = RecordBuilder.create(instance -> instance.group(
            PlacedFeature.CODEC.fieldOf("feature").forGetter(WeightedFeature.class, WeightedFeature::feature),
            Codecs.FLOAT.fieldOf("chance").forGetter(WeightedFeature.class, WeightedFeature::chance)
        ).apply(instance, WeightedFeature::new)).describe("WeightedFeature");
    }

    /**
     * Configuration record for the random feature.
     *
     * @param features       The list of probability-gated features to evaluate.
     * @param defaultFeature The fallback feature executed if all primary features fail.
     */
    public record Config(List<WeightedFeature> features, PlacedFeature defaultFeature) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = RecordBuilder.create(instance -> instance.group(
            WeightedFeature.CODEC.list().fieldOf("features").forGetter(Config.class, Config::features),
            PlacedFeature.CODEC.fieldOf("default_feature").forGetter(Config.class, Config::defaultFeature)
        ).apply(instance, Config::new)).describe("RandomFeatureConfig");
    }
}