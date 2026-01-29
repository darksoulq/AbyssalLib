package com.github.darksoulq.abyssallib.common.util;

@FunctionalInterface
public interface Easing {
    /**
     * Applies the easing function.
     * @param t Current progress between 0.0 and 1.0
     * @return Eased progress (usually between 0.0 and 1.0, but can exceed for elastic/back effects)
     */
    double apply(double t);
}