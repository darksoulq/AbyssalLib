package com.github.darksoulq.abyssallib.common.color.gradient;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;

public class WaveGradient implements ColorProvider {
    private final ColorProvider base;
    private final Vector direction;
    private final double wavelength;
    private final double speed;

    public WaveGradient(ColorProvider base, Vector direction, double wavelength, double speed) {
        this.base = base;
        this.direction = direction.normalize();
        this.wavelength = wavelength;
        this.speed = speed;
    }

    @Override
    public Color get(Vector pos, double progress) {
        double dot = pos.dot(direction);
        double phase = (dot / wavelength) + (progress * speed);
        double t = (Math.sin(phase * Math.PI * 2) + 1) / 2.0;
        return base.get(pos, t);
    }
}