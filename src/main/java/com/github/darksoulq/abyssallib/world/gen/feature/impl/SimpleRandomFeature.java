package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
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
 * A meta-feature that randomly selects exactly one sub-feature from a provided
 * list, giving each option an equal probability of selection.
 */
public class SimpleRandomFeature extends Feature<SimpleRandomFeature.Config> {

    /**
     * Constructs a new SimpleRandomFeature with its associated configuration codec.
     */
    public SimpleRandomFeature() {
        super(Config.CODEC);
    }

    /**
     * Selects one feature from the configured list and delegates placement to it.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if the selected feature successfully placed blocks.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        List<PlacedFeature> features = context.config().features();
        if (features.isEmpty()) return false;

        PlacedFeature selection = features.get(context.random().nextInt(features.size()));
        return selection.place(context.level(), context.random(), context.origin());
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @param config The configuration containing the sub-features.
     * @return The dynamic generation phase of the underlying selected feature.
     */
    @SuppressWarnings("unchecked")
    public GenerationPhase getPhase(Config config) {
        if (config.features().isEmpty()) {
            return GenerationPhase.LOCAL_MODIFICATIONS;
        }
        PlacedFeature first = config.features().get(0);
        Feature<FeatureConfig> rawFeature = (Feature<FeatureConfig>) first.feature().feature();
        return rawFeature.getPhase(first.feature().config());
    }

    /**
     * Configuration record for the simple random feature.
     *
     * @param features The pool of potential features to select from.
     */
    public record Config(List<PlacedFeature> features) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the configuration.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                List<PlacedFeature> features = PlacedFeature.CODEC.list().decode(ops, map.get(ops.createString("features")));
                return new Config(features);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("features"), PlacedFeature.CODEC.list().encode(ops, value.features));
                return ops.createMap(map);
            }
        };
    }
}