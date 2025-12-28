package com.github.darksoulq.abyssallib.world.particle.old;

import com.github.darksoulq.abyssallib.world.particle.Generator;
import org.bukkit.Location;

import java.util.List;

/**
 * Use {@link Generator} instead
 */
@Deprecated(forRemoval = true, since = "v1.8.0-mc1.21.9")
public interface Shape {
    List<Location> points(Location origin, long tick, Particles context);
}
