package com.github.darksoulq.abyssallib.world.particle;

import org.bukkit.Location;

import java.util.List;

public interface Shape {
    List<Location> points(Location origin, long tick, Particles context);
}
