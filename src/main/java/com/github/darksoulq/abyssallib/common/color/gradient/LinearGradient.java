package com.github.darksoulq.abyssallib.common.color.gradient;

import org.bukkit.Color;

/**
 * A standard implementation of a linear color gradient.
 */
public class LinearGradient extends AbstractGradient {
    /**
     * Constructs a LinearGradient with evenly spaced colors.
     * @param colors The varargs {@link Color} stops.
     */
    public LinearGradient(Color... colors) {
        super(colors);
    }

    /**
     * Constructs a LinearGradient with specific color stop positions.
     * @param colors    The array of {@link Color} stops.
     * @param positions The array of positions (0.0 to 1.0).
     */
    public LinearGradient(Color[] colors, float[] positions) {
        super(colors, positions);
    }
}