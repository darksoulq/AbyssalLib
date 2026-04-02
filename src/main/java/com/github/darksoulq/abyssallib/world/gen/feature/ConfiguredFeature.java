package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;

import java.util.Random;

/**
 * Represents a world generation feature strictly bound to its specific configuration.
 * <p>
 * This wrapper unites the stateless procedural logic of a {@link Feature} with the
 * data-driven parameters of a {@link FeatureConfig}, creating an executable unit
 * ready for placement in the world.
 *
 * @param feature The base feature logic.
 * @param config  The parameters governing the feature's behavior.
 * @param <C>     The specific configuration type.
 * @param <F>     The specific feature type.
 */
public record ConfiguredFeature<C extends FeatureConfig, F extends Feature<C>>(F feature, C config) {

    /**
     * Executes the feature's placement logic at the specified origin.
     *
     * @param level  The world generation accessor.
     * @param origin The absolute starting location for the feature.
     * @param random The deterministic random source.
     * @return True if the feature was successfully placed.
     */
    public boolean place(WorldGenAccess level, Location origin, Random random) {
        return feature.place(new FeaturePlaceContext<>(level, origin, random, config));
    }
}