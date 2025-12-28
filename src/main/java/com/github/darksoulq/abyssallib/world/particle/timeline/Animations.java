package com.github.darksoulq.abyssallib.world.particle.timeline;

import com.github.darksoulq.abyssallib.world.particle.Transformer;
import org.bukkit.util.Vector;

public class Animations {

    /**
     * Rotates around the Y axis with easing.
     * @param totalDegrees Total rotation in degrees
     * @param duration Duration in ticks
     * @param easing Easing function
     * @return The formed Transformer
     */
    public static Transformer spinY(double totalDegrees, long duration, Easing easing) {
        return (v, tick) -> {
            double progress = (double) tick / duration;
            double currentDeg = easing.apply(progress) * totalDegrees;
            return v.rotateAroundY(Math.toRadians(currentDeg));
        };
    }

    /**
     * Rotates around the X axis with easing.
     * @param totalDegrees Total rotation in degrees
     * @param duration Duration in ticks
     * @param easing Easing function
     * @return a {@link Transformer}
     */
    public static Transformer spinX(double totalDegrees, long duration, Easing easing) {
        return (v, tick) -> {
            double progress = (double) tick / duration;
            double currentDeg = easing.apply(progress) * totalDegrees;
            return v.rotateAroundX(Math.toRadians(currentDeg));
        };
    }

    /**
     * Rotates around the Z axis with easing.
     * @param totalDegrees Total rotation in degrees
     * @param duration Duration in ticks
     * @param easing Easing function
     * @return a {@link Transformer}
     */
    public static Transformer spinZ(double totalDegrees, long duration, Easing easing) {
        return (v, tick) -> {
            double progress = (double) tick / duration;
            double currentDeg = easing.apply(progress) * totalDegrees;
            return v.rotateAroundZ(Math.toRadians(currentDeg));
        };
    }

    /**
     * Scales a vector from {@code startScale} to {@code endScale}.
     *
     * @param startScale scale factor at tick {@code 0}
     * @param endScale scale factor at tick {@code duration}
     * @param duration duration of the animation in ticks
     * @param easing easing function controlling interpolation
     * @return a {@link Transformer}
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
     * Translates (moves) a vector by interpolating toward a target offset.
     *
     * @param totalOffset final offset applied at the end of the animation
     * @param duration duration of the animation in ticks
     * @param easing easing function controlling interpolation
     * @return a {@link Transformer}
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
     * Creates a breathing (pulsing) scale animation using a sine wave.
     *
     * <p>
     * This animation ignores standard easing functions, as the sine curve
     * already defines its own smooth interpolation.
     * </p>
     *
     * @param minScale minimum scale value
     * @param maxScale maximum scale value
     * @param duration duration of one full oscillation cycle in ticks
     * @return a {@link Transformer}
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