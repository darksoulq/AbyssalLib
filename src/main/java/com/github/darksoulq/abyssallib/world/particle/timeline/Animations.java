package com.github.darksoulq.abyssallib.world.particle.timeline;

import com.github.darksoulq.abyssallib.common.util.Easing;
import com.github.darksoulq.abyssallib.world.particle.Transformer;
import org.bukkit.util.Vector;

/**
 * A utility class providing factory methods for time-aware {@link Transformer} animations.
 * <p>
 * These animations utilize {@link Easing} functions to interpolate spatial properties
 * (rotation, scale, translation) over a defined duration.
 */
public class Animations {

    /**
     * Creates an animation that rotates particles around the Y-axis over time.
     *
     * @param totalDegrees The total amount of rotation to perform (e.g., 360 for a full spin).
     * @param duration     The lifespan of the animation in server ticks.
     * @param easing       The {@link Easing} function used to determine the rotation curve.
     * @return A {@link Transformer} that applies eased Y-axis rotation based on current tick.
     */
    public static Transformer spinY(double totalDegrees, long duration, Easing easing) {
        return (v, tick) -> {
            double progress = (double) tick / duration;
            double currentDeg = easing.apply(progress) * totalDegrees;
            return v.rotateAroundY(Math.toRadians(currentDeg));
        };
    }

    /**
     * Creates an animation that rotates particles around the X-axis over time.
     *
     * @param totalDegrees The total amount of rotation in degrees.
     * @param duration     The lifespan of the animation in server ticks.
     * @param easing       The {@link Easing} function used to determine the rotation curve.
     * @return A {@link Transformer} that applies eased X-axis rotation based on current tick.
     */
    public static Transformer spinX(double totalDegrees, long duration, Easing easing) {
        return (v, tick) -> {
            double progress = (double) tick / duration;
            double currentDeg = easing.apply(progress) * totalDegrees;
            return v.rotateAroundX(Math.toRadians(currentDeg));
        };
    }

    /**
     * Creates an animation that rotates particles around the Z-axis over time.
     *
     * @param totalDegrees The total amount of rotation in degrees.
     * @param duration     The lifespan of the animation in server ticks.
     * @param easing       The {@link Easing} function used to determine the rotation curve.
     * @return A {@link Transformer} that applies eased Z-axis rotation based on current tick.
     */
    public static Transformer spinZ(double totalDegrees, long duration, Easing easing) {
        return (v, tick) -> {
            double progress = (double) tick / duration;
            double currentDeg = easing.apply(progress) * totalDegrees;
            return v.rotateAroundZ(Math.toRadians(currentDeg));
        };
    }

    /**
     * Scales the particle coordinates from a starting multiplier to an ending multiplier.
     *
     * @param startScale The initial scale factor applied at tick 0.
     * @param endScale   The final scale factor applied at the end of the duration.
     * @param duration   The total time in ticks for the scaling process to complete.
     * @param easing     The {@link Easing} function controlling the interpolation speed.
     * @return A {@link Transformer} that modifies the vector magnitude over time.
     */
    public static Transformer scale(double startScale, double endScale, long duration, Easing easing) {
        return (v, tick) -> {
            double progress = (double) tick / duration;
            double eased = easing.apply(progress);
            double currentScale = startScale + (eased * (endScale - startScale));
            return v.multiply(currentScale);
        };
    }

    /**
     * Translates (moves) the particles toward a target offset vector.
     *
     * @param totalOffset The final {@link Vector} offset that will be applied by the end of the duration.
     * @param duration    The total time in ticks for the translation to reach the final offset.
     * @param easing      The {@link Easing} function controlling the movement interpolation.
     * @return A {@link Transformer} that shifts coordinates over time.
     */
    public static Transformer translate(Vector totalOffset, long duration, Easing easing) {
        return (v, tick) -> {
            double progress = (double) tick / duration;
            double eased = easing.apply(progress);
            Vector currentOffset = totalOffset.clone().multiply(eased);
            return v.add(currentOffset);
        };
    }

    /**
     * Creates a cyclical "breathing" animation that pulses the scale using a sine wave.
     * <p>
     * Unlike other animations in this class, this does not use an external Easing function
     * as the harmonic motion is inherently defined by the sine curve.
     * </p>
     *
     * @param minScale The minimum scale factor at the bottom of the pulse.
     * @param maxScale The maximum scale factor at the peak of the pulse.
     * @param duration The number of ticks required to complete one full oscillation (inhale and exhale).
     * @return A {@link Transformer} that applies a cyclical scaling effect.
     */
    public static Transformer breathe(double minScale, double maxScale, long duration) {
        return (v, tick) -> {
            double progress = (double) tick / duration;
            double angle = progress * Math.PI * 2;
            double sine = Math.sin(angle);
            double scale = minScale + ((sine + 1) / 2) * (maxScale - minScale);
            return v.multiply(scale);
        };
    }
}