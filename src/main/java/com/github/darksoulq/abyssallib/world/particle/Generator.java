package com.github.darksoulq.abyssallib.world.particle;

import org.bukkit.util.Vector;

import java.util.List;

@FunctionalInterface
public interface Generator {
    List<Vector> generate(long tick);
}