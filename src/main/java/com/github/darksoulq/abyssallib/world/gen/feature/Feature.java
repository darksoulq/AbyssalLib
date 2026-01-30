package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * The base class for all world generation features (e.g., trees, ore veins, lakes).
 * <p>
 * A Feature represents the raw procedural logic used to place blocks. It is
 * stateless and relies on a {@link FeatureConfig} to determine its behavior.
 * </p>
 *
 * @param <C> The type of {@link FeatureConfig} used by this feature.
 */
public abstract class Feature<C extends FeatureConfig> {
    /** The codec used to serialize and deserialize the feature's configuration. */
    private final Codec<C> codec;

    /**
     * Constructs a new Feature.
     *
     * @param codec The {@link Codec} for the associated configuration type.
     */
    public Feature(Codec<C> codec) {
        this.codec = codec;
    }

    /**
     * Executes the generation logic to place blocks in the world.
     *
     * @param context The {@link FeaturePlaceContext} containing world access,
     * random source, origin location, and specific config.
     * @return {@code true} if the feature was successfully generated at the location.
     */
    public abstract boolean place(FeaturePlaceContext<C> context);

    /**
     * @return The {@link Codec} instance for this feature's configuration.
     */
    public Codec<C> getCodec() {
        return codec;
    }
}