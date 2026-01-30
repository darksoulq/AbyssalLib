package com.github.darksoulq.abyssallib.common.color.gradient;

import org.bukkit.Color;
import org.bukkit.util.Vector;

/**
 * A gradient that radiates outward from the origin (0,0) on the XZ plane.
 */
public class RadialGradient extends AbstractGradient {
    /** The distance from the origin at which the gradient reaches the final color stop. */
    private final double radius;

    /**
     * Constructs a RadialGradient.
     *
     * @param radius The radius of the gradient.
     * @param colors The varargs {@link Color} stops.
     */
    public RadialGradient(double radius, Color... colors) {
        super(colors);
        this.radius = radius;
    }

    /**
     * Calculates color based on the distance from the origin relative to the radius.
     *
     * @param position The spatial {@link Vector} position.
     * @param progress The global progress (unused).
     * @return The radial {@link Color}.
     */
    @Override
    public Color get(Vector position, double progress) {
        double dist = Math.sqrt(position.getX() * position.getX() + position.getZ() * position.getZ());
        double t = Math.min(1.0, dist / radius);
        return getAt(t);
    }
}