package com.github.darksoulq.abyssallib.world.gen.feature;

/**
 * An interface representing the configuration parameters for a {@link Feature}.
 * <p>
 * Implementations of this interface (usually records) hold the data needed by
 * a feature's logic, such as block types, vein sizes, or height ranges.
 * </p>
 */
public interface FeatureConfig {
    /** A default empty configuration for features that require no parameters. */
    FeatureConfig NONE = new FeatureConfig() {};
}