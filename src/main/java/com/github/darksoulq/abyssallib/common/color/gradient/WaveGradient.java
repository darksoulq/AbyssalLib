package com.github.darksoulq.abyssallib.common.color.gradient;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;

/**
 * A color provider that applies a sinusoidal wave modulation to a base color provider.
 */
public class WaveGradient implements ColorProvider {
    /** The base provider used for the color stops. */
    private final ColorProvider base;
    /** The normalized direction in which the wave propagates. */
    private final Vector direction;
    /** The distance between consecutive wave peaks. */
    private final double wavelength;
    /** The speed at which the wave cycles based on the progress parameter. */
    private final double speed;

    /**
     * Constructs a WaveGradient.
     *
     * @param base       The {@link ColorProvider} to modulate.
     * @param direction  The {@link Vector} of propagation.
     * @param wavelength The spatial frequency.
     * @param speed      The temporal frequency.
     */
    public WaveGradient(ColorProvider base, Vector direction, double wavelength, double speed) {
        this.base = base;
        this.direction = direction.normalize();
        this.wavelength = wavelength;
        this.speed = speed;
    }

    /**
     * Calculates color based on a sine wave oscillating between 0.0 and 1.0.
     *
     * @param pos      The spatial {@link Vector} position.
     * @param progress The temporal progress.
     * @return The wave-modulated {@link Color}.
     */
    @Override
    public Color get(Vector pos, double progress) {
        double dot = pos.dot(direction);
        double phase = (dot / wavelength) + (progress * speed);
        double t = (Math.sin(phase * Math.PI * 2) + 1) / 2.0;
        return base.get(pos, t);
    }
}