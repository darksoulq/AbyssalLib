package com.github.darksoulq.abyssallib.particle;

import org.bukkit.Location;

import java.util.List;

/**
 * Represents a particle shape that determines how particle positions
 * are calculated relative to an origin point.
 * <p>
 * Shapes can optionally support animation by overriding the {@link #animate} method.
 */
public interface Shape {
    /**
     * Returns a list of points (as {@link Location}) relative to the given origin,
     * where particles will be spawned.
     *
     * @param origin the base location used for calculating the shape
     * @return list of particle spawn locations
     */
    List<Location> points(Location origin);

    /**
     * (Optional) Animates the shape over time.
     * Override this method to apply dynamic transformations such as rotation or movement.
     *
     * @param builder the {@link Particles} instance using this shape
     * @param origin  the original location of the particle effect
     * @param tick    the number of ticks since the effect started
     */
    default void animate(Particles builder, Location origin, long tick) {
    }
}
