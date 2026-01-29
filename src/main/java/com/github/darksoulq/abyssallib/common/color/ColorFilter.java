package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface ColorFilter extends UnaryOperator<Color> {

    default ColorFilter andThen(ColorFilter after) {
        return c -> after.apply(apply(c));
    }

    static ColorFilter grayscale() {
        return ColorUtils::grayscale;
    }

    static ColorFilter invert() {
        return ColorUtils::invert;
    }

    static ColorFilter sepia() {
        return c -> {
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            int nr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
            int ng = (int) (0.349 * r + 0.686 * g + 0.168 * b);
            int nb = (int) (0.272 * r + 0.534 * g + 0.131 * b);
            return Color.fromRGB(Math.min(255, nr), Math.min(255, ng), Math.min(255, nb));
        };
    }

    static ColorFilter brightness(float factor) {
        return c -> ColorUtils.brighten(c, factor);
    }

    static ColorFilter saturation(float factor) {
        return c -> ColorUtils.saturate(c, factor);
    }

    static ColorFilter contrast(float factor) {
        return c -> {
            float[] rgb = {c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f};
            for (int i = 0; i < 3; i++) {
                rgb[i] = ((rgb[i] - 0.5f) * factor) + 0.5f;
                rgb[i] = Math.max(0, Math.min(1, rgb[i]));
            }
            return Color.fromRGB((int)(rgb[0] * 255), (int)(rgb[1] * 255), (int)(rgb[2] * 255));
        };
    }

    static ColorFilter tint(Color tint, double intensity) {
        return c -> ColorUtils.mix(c, tint, intensity);
    }

    static ColorFilter colorBlindness(ColorBlindness type) {
        return type::simulate;
    }
}