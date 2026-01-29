package com.github.darksoulq.abyssallib.common.color.gradient;

import org.bukkit.Color;
import org.bukkit.util.Vector;

public class RadialGradient extends AbstractGradient {
    private final double radius;

    public RadialGradient(double radius, Color... colors) {
        super(colors);
        this.radius = radius;
    }

    @Override
    public Color get(Vector position, double progress) {
        double dist = Math.sqrt(position.getX() * position.getX() + position.getZ() * position.getZ());
        double t = Math.min(1.0, dist / radius);
        return getAt(t);
    }
}