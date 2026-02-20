package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * The base class for all world generation features such as trees, ore veins, or lakes.
 * A Feature represents the raw procedural logic used to place blocks within the world.
 * It is designed to be stateless, relying entirely on a {@link FeatureConfig} to
 * determine its specific behavior and parameters during the generation process.
 *
 * @param <C>
 * The type of {@link FeatureConfig} utilized by this feature implementation.
 */
public abstract class Feature<C extends FeatureConfig> {

    /**
     * The codec used to serialize and deserialize the configuration associated with this feature.
     */
    private final Codec<C> codec;

    /**
     * Constructs a new Feature with a specific configuration codec.
     *
     * @param codec
     * The {@link Codec} used for the associated configuration type {@code C}.
     */
    public Feature(Codec<C> codec) {
        this.codec = codec;
    }

    /**
     * Executes the procedural generation logic to place blocks in the world.
     *
     * @param context
     * The {@link FeaturePlaceContext} containing world access, random source,
     * origin location, and the specific configuration instance.
     * @return
     * True if the feature was successfully generated at the location, false otherwise.
     */
    public abstract boolean place(FeaturePlaceContext<C> context);

    /**
     * Retrieves the codec instance responsible for handling this feature's configuration data.
     * This is primarily used for data-driven world generation where features are
     * defined in external JSON or NBT files.
     *
     * @return
     * The {@link Codec} instance for this feature's configuration.
     */
    public Codec<C> getCodec() {
        return codec;
    }
}