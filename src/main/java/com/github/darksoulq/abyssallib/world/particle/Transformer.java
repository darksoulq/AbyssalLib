package com.github.darksoulq.abyssallib.world.particle;

import org.bukkit.util.Vector;

/**
 * Functional interface for modifying particle coordinates during the animation pipeline.
 * <p>
 * Transformers are applied sequentially to allow for complex movement like
 * combined rotation, scaling, and translation.
 */
@FunctionalInterface
public interface Transformer {
    /**
     * Transforms a vector coordinate based on the current animation tick.
     *
     * @param input The original coordinate vector.
     * @param tick  The current animation tick.
     * @return The transformed coordinate {@link Vector}.
     */
    Vector transform(Vector input, long tick);
}