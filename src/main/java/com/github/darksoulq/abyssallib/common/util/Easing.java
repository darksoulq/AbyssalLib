package com.github.darksoulq.abyssallib.common.util;

/**
 * A functional interface representing an interpolation function.
 * <p>
 * Easing functions specify the rate of change of a parameter over time,
 * allowing for natural-looking animations (acceleration and deceleration).
 */
@FunctionalInterface
public interface Easing {
    /**
     * Applies the easing transformation to the given input.
     *
     * @param t The input value, typically normalized between 0.0 and 1.0,
     * representing the percentage of completion.
     * @return The transformed value, usually between 0.0 and 1.0 (though some
     * functions like Back or Elastic may exceed these bounds).
     */
    double apply(double t);
}