package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;

import java.util.Random;

/**
 * A record representing a {@link Feature} paired with a specific {@link FeatureConfig}.
 * <p>
 * While a Feature defines the "how" (e.g., "how to build an ore vein"), a
 * ConfiguredFeature defines the "what" (e.g., "an iron ore vein of size 9").
 * </p>
 *
 * @param <FC> The configuration type.
 * @param <F>  The feature type.
 */
public record ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>>(
    F feature,
    FC config
) {

    /**
     * Wraps the provided parameters into a context and triggers the underlying feature placement.
     *
     * @param level  The world generation accessor.
     * @param origin The location where the feature should start generating.
     * @param random The random source.
     * @return {@code true} if generation was successful.
     */
    public boolean place(WorldGenAccess level, Location origin, Random random) {
        return feature.place(new FeaturePlaceContext<>(level, origin, random, config));
    }
}