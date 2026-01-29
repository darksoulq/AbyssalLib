package com.github.darksoulq.abyssallib.common.color.gradient;

import org.bukkit.Color;

public class LinearGradient extends AbstractGradient {
    public LinearGradient(Color... colors) {
        super(colors);
    }

    public LinearGradient(Color[] colors, float[] positions) {
        super(colors, positions);
    }
}