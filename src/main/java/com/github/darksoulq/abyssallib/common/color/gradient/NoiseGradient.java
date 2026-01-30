package com.github.darksoulq.abyssallib.common.color.gradient;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;

/**
 * A color provider that uses 3D Perlin noise to sample from a base ColorProvider.
 */
public class NoiseGradient implements ColorProvider {
    /** The base color provider to sample from. */
    private final ColorProvider base;
    /** The frequency of the noise (higher values result in more granular noise). */
    private final double frequency;
    /** The internal Perlin noise generator. */
    private final PerlinNoiseGenerator generator;

    /**
     * Constructs a NoiseGradient.
     *
     * @param base      The {@link ColorProvider} to use for final color output.
     * @param frequency The scale of the noise.
     */
    public NoiseGradient(ColorProvider base, double frequency) {
        this.base = base;
        this.frequency = frequency;
        this.generator = new PerlinNoiseGenerator(System.currentTimeMillis());
    }

    /**
     * Generates a noise value for the position and uses it as the interpolation factor for the base provider.
     *
     * @param pos      The spatial {@link Vector} position.
     * @param progress The global progress.
     * @return The noise-influenced {@link Color}.
     */
    @Override
    public Color get(Vector pos, double progress) {
        double noise = generator.noise(pos.getX() * frequency, pos.getY() * frequency, pos.getZ() * frequency);
        double t = (noise + 1) / 2.0;
        return base.get(pos, t);
    }
}