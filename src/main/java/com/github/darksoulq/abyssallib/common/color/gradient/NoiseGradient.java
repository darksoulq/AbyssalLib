package com.github.darksoulq.abyssallib.common.color.gradient;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;

public class NoiseGradient implements ColorProvider {
    private final ColorProvider base;
    private final double frequency;
    private final PerlinNoiseGenerator generator;

    public NoiseGradient(ColorProvider base, double frequency) {
        this.base = base;
        this.frequency = frequency;
        this.generator = new PerlinNoiseGenerator(System.currentTimeMillis());
    }

    @Override
    public Color get(Vector pos, double progress) {
        double noise = generator.noise(pos.getX() * frequency, pos.getY() * frequency, pos.getZ() * frequency);
        double t = (noise + 1) / 2.0; 
        return base.get(pos, t);
    }
}