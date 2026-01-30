package com.github.darksoulq.abyssallib.world.particle;

import org.bukkit.util.Vector;

import java.util.List;

/**
 * Functional interface responsible for defining the base geometric shape of an effect.
 * <p>
 * Generators produce a collection of relative coordinates which are later
 * transformed and rendered.
 */
@FunctionalInterface
public interface Generator {
    /**
     * Produces a list of relative vectors for a specific tick.
     *
     * @param tick The current animation tick.
     * @return A {@link List} of relative {@link Vector} coordinates.
     */
    List<Vector> generate(long tick);
}