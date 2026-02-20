package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;

import java.util.Random;

/**
 * A record representing a {@link Feature} paired with a specific {@link FeatureConfig}.
 * While a Feature defines the "how" (e.g., "how to build an ore vein"), a
 * ConfiguredFeature defines the "what" (e.g., "an iron ore vein of size 9").
 *
 * @param <FC>
 * The configuration type that extends {@link FeatureConfig}.
 * @param <F>
 * The feature type that extends {@link Feature} using configuration {@code FC}.
 * @param feature
 * The stateless feature logic used for generation.
 * @param config
 * The specific parameters and data used by the feature logic.
 */
public record ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>>(
    F feature,
    FC config
) {

    /**
     * Wraps the provided parameters into a context and triggers the underlying feature placement.
     *
     * @param level
     * The {@link WorldGenAccess} providing thread-safe world modification during generation.
     * @param origin
     * The {@link Location} where the feature should start its generation logic.
     * @param random
     * The {@link Random} source used to ensure procedural variety.
     * @return
     * True if the generation was successful at the specified location, false otherwise.
     */
    public boolean place(WorldGenAccess level, Location origin, Random random) {
        return feature.place(new FeaturePlaceContext<>(level, origin, random, config));
    }
}