package com.github.darksoulq.abyssallib.world.gen.feature;

import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;

import java.util.Random;

public record FeaturePlaceContext<C extends FeatureConfig>(WorldGenAccess level, Location origin, Random random, C config) {}