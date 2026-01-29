package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

public final class ColorHarmony {

    public static ColorPalette monochromatic(Color base) {
        return new ColorPalette(ColorUtils.monochromatic(base));
    }

    public static ColorPalette analogous(Color base) {
        return new ColorPalette(ColorUtils.analogous(base));
    }

    public static ColorPalette complementary(Color base) {
        return new ColorPalette(base, ColorUtils.complementary(base));
    }

    public static ColorPalette splitComplementary(Color base) {
        return new ColorPalette(ColorUtils.splitComplementary(base));
    }

    public static ColorPalette triadic(Color base) {
        return new ColorPalette(ColorUtils.triadic(base));
    }

    public static ColorPalette tetradic(Color base) {
        return new ColorPalette(ColorUtils.tetradic(base));
    }

    public static ColorPalette shades(Color base, int steps) {
        Color black = Color.BLACK;
        return new ColorPalette(ColorUtils.gradient(base, black, steps));
    }

    public static ColorPalette tints(Color base, int steps) {
        Color white = Color.WHITE;
        return new ColorPalette(ColorUtils.gradient(base, white, steps));
    }

    public static ColorPalette tones(Color base, int steps) {
        Color gray = Color.GRAY;
        return new ColorPalette(ColorUtils.gradient(base, gray, steps));
    }
}