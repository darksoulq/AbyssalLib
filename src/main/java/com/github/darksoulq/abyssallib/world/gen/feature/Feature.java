package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.world.gen.feature.impl.StructureFeature;

public abstract class Feature<C extends FeatureConfig> {
    private final Codec<C> codec;

    public Feature(Codec<C> codec) {
        this.codec = codec;
    }

    /**
     * Places the feature in the world.
     *
     * @param context The placement context containing world access, position, and config.
     * @return true if the feature was successfully placed.
     */
    public abstract boolean place(FeaturePlaceContext<C> context);

    public Codec<C> getCodec() {
        return codec;
    }
}