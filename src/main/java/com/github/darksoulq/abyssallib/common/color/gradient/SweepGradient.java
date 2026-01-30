package com.github.darksoulq.abyssallib.common.color.gradient;

import org.bukkit.Color;
import org.bukkit.util.Vector;

/**
 * A gradient that sweeps around the origin like a radar or clock hand on the XZ plane.
 */
public class SweepGradient extends AbstractGradient {
    /**
     * Constructs a SweepGradient.
     * @param colors The varargs {@link Color} stops.
     */
    public SweepGradient(Color... colors) {
        super(colors);
    }

    /**
     * Calculates color based on the angular position of the vector around the Y axis.
     *
     * @param position The spatial {@link Vector} position.
     * @param progress The global progress (unused).
     * @return The angular {@link Color}.
     */
    @Override
    public Color get(Vector position, double progress) {
        double angle = Math.atan2(position.getZ(), position.getX());
        double t = (angle + Math.PI) / (2 * Math.PI);
        return getAt(t);
    }
}