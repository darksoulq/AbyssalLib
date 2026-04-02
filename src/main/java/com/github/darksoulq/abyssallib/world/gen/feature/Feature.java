package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * The base class for all world generation features such as trees, ore veins, or lakes.
 * A Feature represents the raw procedural logic used to place blocks within the world.
 * It is designed to be stateless, relying entirely on a {@link FeatureConfig} to
 * determine its specific behavior and parameters during the generation process.
 *
 * @param <C> The type of {@link FeatureConfig} utilized by this feature implementation.
 */
public abstract class Feature<C extends FeatureConfig> {

    /**
     * The codec used to serialize and deserialize the configuration associated with this feature.
     */
    private final Codec<C> codec;

    /**
     * Constructs a new Feature with a specific configuration codec.
     *
     * @param codec The {@link Codec} used for the associated configuration type {@code C}.
     */
    public Feature(Codec<C> codec) {
        this.codec = codec;
    }

    /**
     * Executes the procedural generation logic to place blocks in the world.
     *
     * @param context The {@link FeaturePlaceContext} containing world access, random source,
     * origin location, and the specific configuration instance.
     * @return True if the feature was successfully generated at the location, false otherwise.
     */
    public abstract boolean place(FeaturePlaceContext<C> context);

    /**
     * Declares the specific chronological step this feature should generate in.
     * <p>
     * Override this method in subclasses to assign them to the correct phase
     * (e.g., Ores should return UNDERGROUND_ORES, Trees should return VEGETAL_DECORATION).
     *
     * @return The generation phase for this feature.
     */
    public GenerationPhase getPhase(C config) {
        return GenerationPhase.LOCAL_MODIFICATIONS;
    }

    /**
     * Retrieves the codec instance responsible for handling this feature's configuration data.
     *
     * @return The {@link Codec} instance for this feature's configuration.
     */
    public Codec<C> getCodec() {
        return codec;
    }
}