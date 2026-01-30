package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;

import java.util.Random;

/**
 * A data container (record) provided to a {@link Feature} during the placement phase.
 *
 * @param <C>    The configuration type.
 * @param level  The {@link WorldGenAccess} used to modify the world safely.
 * @param origin The starting {@link Location} for the feature's generation.
 * @param random The {@link Random} source, seeded for deterministic generation.
 * @param config The specific {@link FeatureConfig} instance for this pass.
 */
public record FeaturePlaceContext<C extends FeatureConfig>(
    WorldGenAccess level,
    Location origin,
    Random random,
    C config
) {}