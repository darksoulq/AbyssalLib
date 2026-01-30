package com.github.darksoulq.abyssallib.common.util;

/**
 * A collection of standard easing functions implemented as an enum.
 * <p>
 * These functions are based on common animation formulas used to create
 * smooth transitions.
 */
public enum Easings implements Easing {
    /** A simple linear transition where the rate of change is constant. */
    LINEAR(t -> t),

    /** Quadratic acceleration; starts slow and speeds up. */
    IN_QUAD(t -> t * t),

    /** Quadratic deceleration; starts fast and slows down. */
    OUT_QUAD(t -> t * (2 - t)),

    /** Quadratic acceleration/deceleration; slow start and end, fast in the middle. */
    IN_OUT_QUAD(t -> t < .5 ? 2 * t * t : -1 + (4 - 2 * t) * t),

    /** Cubic acceleration; starts very slow and speeds up rapidly. */
    IN_CUBIC(t -> t * t * t),

    /** Cubic deceleration; starts very fast and slows down heavily. */
    OUT_CUBIC(t -> (--t) * t * t + 1),

    /** Cubic acceleration/deceleration; more pronounced than Quadratic In-Out. */
    IN_OUT_CUBIC(t -> t < .5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1),

    /** * Starts by moving slightly backward before accelerating forward.
     * Uses a standard overshoot constant of 1.70158.
     */
    IN_BACK(t -> {
        double s = 1.70158;
        return t * t * ((s + 1) * t - s);
    }),

    /** * Overshoots the target value slightly before settling back to 1.0.
     */
    OUT_BACK(t -> {
        double s = 1.70158;
        return --t * t * ((s + 1) * t + s) + 1;
    }),

    /** * An oscillating transition that simulates an elastic rubber band effect,
     * bouncing past the end point before settling.
     */
    OUT_ELASTIC(t -> {
        if (t == 0) return 0;
        if (t == 1) return 1;
        double p = 0.3;
        return Math.pow(2, -10 * t) * Math.sin((t - p / 4) * (2 * Math.PI) / p) + 1;
    });

    /** The internal easing function logic. */
    private final Easing function;

    /**
     * Constructs the easing enum constant with its respective function.
     *
     * @param function The {@link Easing} implementation.
     */
    Easings(Easing function) {
        this.function = function;
    }

    /**
     * Applies the specific easing formula of this constant to the input.
     *
     * @param t The normalized time input (0.0 to 1.0).
     * @return The interpolated result.
     */
    @Override
    public double apply(double t) {
        return function.apply(t);
    }
}