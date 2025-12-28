package com.github.darksoulq.abyssallib.world.particle.style;

import org.bukkit.Color;

public class Gradient {
    private final Color[] colors;

    public Gradient(Color... colors) {
        this.colors = colors;
    }

    /**
     * Gets the color at a specific point in the gradient (0.0 to 1.0)
     */
    public Color get(double progress) {
        if (progress <= 0) return colors[0];
        if (progress >= 1) return colors[colors.length - 1];
        double scaled = progress * (colors.length - 1);
        int idx = (int) scaled;
        double subProgress = scaled - idx;

        return mix(colors[idx], colors[idx + 1], subProgress);
    }

    private Color mix(Color c1, Color c2, double percent) {
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * percent);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * percent);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * percent);
        return Color.fromRGB(r, g, b);
    }
}