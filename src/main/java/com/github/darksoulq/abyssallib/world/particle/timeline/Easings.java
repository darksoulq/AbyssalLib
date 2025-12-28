package com.github.darksoulq.abyssallib.world.particle.timeline;

public enum Easings implements Easing {
    LINEAR(t -> t),

    IN_QUAD(t -> t * t),
    OUT_QUAD(t -> t * (2 - t)),
    IN_OUT_QUAD(t -> t < .5 ? 2 * t * t : -1 + (4 - 2 * t) * t),

    IN_CUBIC(t -> t * t * t),
    OUT_CUBIC(t -> (--t) * t * t + 1),
    IN_OUT_CUBIC(t -> t < .5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1),

    IN_BACK(t -> {
        double s = 1.70158;
        return t * t * ((s + 1) * t - s);
    }),
    OUT_BACK(t -> {
        double s = 1.70158;
        return --t * t * ((s + 1) * t + s) + 1;
    }),

    OUT_ELASTIC(t -> {
        if (t == 0) return 0;
        if (t == 1) return 1;
        double p = 0.3;
        return Math.pow(2, -10 * t) * Math.sin((t - p / 4) * (2 * Math.PI) / p) + 1;
    });

    private final Easing function;

    Easings(Easing function) {
        this.function = function;
    }

    @Override
    public double apply(double t) {
        return function.apply(t);
    }
}