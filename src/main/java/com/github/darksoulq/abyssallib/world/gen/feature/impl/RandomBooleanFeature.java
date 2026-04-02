package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacedFeature;

import java.util.HashMap;
import java.util.Map;

/**
 * A meta-feature that performs a boolean roll to decide between two distinct sub-features.
 */
public class RandomBooleanFeature extends Feature<RandomBooleanFeature.Config> {

    /**
     * Constructs a new RandomBooleanFeature with its associated configuration codec.
     */
    public RandomBooleanFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the boolean roll and delegates placement to the winning feature.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if the selected feature successfully placed blocks.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        PlacedFeature selection = context.random().nextBoolean() ?
            context.config().featureTrue() :
            context.config().featureFalse();

        return selection.place(context.level(), context.random(), context.origin());
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @param config The configuration containing the sub-features.
     * @return The dynamic generation phase of the underlying feature.
     */
    @SuppressWarnings("unchecked")
    public GenerationPhase getPhase(Config config) {
        PlacedFeature featureTrue = config.featureTrue();
        if (featureTrue != null) {
            Feature<FeatureConfig> rawFeature = (Feature<FeatureConfig>) featureTrue.feature().feature();
            return rawFeature.getPhase(featureTrue.feature().config());
        }
        return GenerationPhase.LOCAL_MODIFICATIONS;
    }

    /**
     * Configuration record for the random boolean feature.
     *
     * @param featureTrue  The feature executed if true.
     * @param featureFalse The feature executed if false.
     */
    public record Config(PlacedFeature featureTrue, PlacedFeature featureFalse) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                PlacedFeature featureTrue = PlacedFeature.CODEC.decode(ops, map.get(ops.createString("feature_true")));
                PlacedFeature featureFalse = PlacedFeature.CODEC.decode(ops, map.get(ops.createString("feature_false")));
                return new Config(featureTrue, featureFalse);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("feature_true"), PlacedFeature.CODEC.encode(ops, value.featureTrue));
                map.put(ops.createString("feature_false"), PlacedFeature.CODEC.encode(ops, value.featureFalse));
                return ops.createMap(map);
            }
        };
    }
}