package com.github.darksoulq.abyssallib.world.particle;

import org.bukkit.util.Vector;

@FunctionalInterface
public interface Transformer {
    Vector transform(Vector input, long tick);
}