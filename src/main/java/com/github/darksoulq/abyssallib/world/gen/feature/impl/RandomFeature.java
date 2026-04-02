package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacedFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        public static final Codec<WeightedFeature> CODEC = new Codec<>() {

            @Override
            public <D> WeightedFeature decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                PlacedFeature feature = PlacedFeature.CODEC.decode(ops, map.get(ops.createString("feature")));
                float chance = Codecs.FLOAT.decode(ops, map.get(ops.createString("chance")));
                return new WeightedFeature(feature, chance);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, WeightedFeature value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("feature"), PlacedFeature.CODEC.encode(ops, value.feature));
                map.put(ops.createString("chance"), Codecs.FLOAT.encode(ops, value.chance));
                return ops.createMap(map);
            }
        };
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
        public static final Codec<Config> CODEC = new Codec<>() {

            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                List<WeightedFeature> features = WeightedFeature.CODEC.list().decode(ops, map.get(ops.createString("features")));
                PlacedFeature defaultFeature = PlacedFeature.CODEC.decode(ops, map.get(ops.createString("default_feature")));
                return new Config(features, defaultFeature);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("features"), WeightedFeature.CODEC.list().encode(ops, value.features));
                map.put(ops.createString("default_feature"), PlacedFeature.CODEC.encode(ops, value.defaultFeature));
                return ops.createMap(map);
            }
        };
    }
}