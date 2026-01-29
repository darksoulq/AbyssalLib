package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;

import java.util.Random;

public record ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>>(F feature, FC config) {

    public boolean place(WorldGenAccess level, Location origin, Random random) {
        return feature.place(new FeaturePlaceContext<>(level, origin, random, config));
    }
}