package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

import java.util.function.UnaryOperator;

/**
 * A functional interface representing a transformation applied to a {@link Color}.
 * <p>
 * Color filters are used to modify color properties such as brightness, saturation,
 * or to apply complex effects like sepia and color-blindness simulation.
 */
@FunctionalInterface
public interface ColorFilter extends UnaryOperator<Color> {

    /**
     * Composes this filter with another. The result of this filter is passed
     * as the input to the {@code after} filter.
     *
     * @param after The filter to apply after this one.
     * @return A composed {@link ColorFilter}.
     */
    default ColorFilter andThen(ColorFilter after) {
        return c -> after.apply(apply(c));
    }

    /**
     * Creates a filter that converts colors to grayscale.
     * <p>Formula: {@code (R + G + B) / 3}</p>
     *
     * @return A grayscale {@link ColorFilter}.
     */
    static ColorFilter grayscale() {
        return ColorUtils::grayscale;
    }

    /**
     * Creates a filter that inverts the RGB components of a color.
     * <p>Formula: {@code 255 - Component}</p>
     *
     * @return An inversion {@link ColorFilter}.
     */
    static ColorFilter invert() {
        return ColorUtils::invert;
    }

    /**
     * Creates a filter that applies a sepia tone effect.
     * <p>
     * Uses the standard W3C recommended coefficients for digital sepia conversion.
     * </p>
     *
     * @return A sepia {@link ColorFilter}.
     */
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

    /**
     * Creates a filter that adjusts the brightness of a color.
     *
     * @param factor The brightness multiplier. Values {@code > 1.0} brighten,
     * values {@code < 1.0} darken.
     * @return A brightness adjustment {@link ColorFilter}.
     */
    static ColorFilter brightness(float factor) {
        return c -> ColorUtils.brighten(c, factor);
    }

    /**
     * Creates a filter that adjusts the saturation of a color.
     *
     * @param factor The saturation multiplier. Values {@code > 1.0} increase intensity,
     * values {@code < 1.0} desaturate.
     * @return A saturation adjustment {@link ColorFilter}.
     */
    static ColorFilter saturation(float factor) {
        return c -> ColorUtils.saturate(c, factor);
    }

    /**
     * Creates a filter that adjusts the contrast of a color.
     * <p>
     * Contrast is adjusted by moving the RGB components toward or away from
     * the 0.5 (50%) midpoint.
     * </p>
     *
     * @param factor The contrast factor. Values {@code > 1.0} increase contrast,
     * values {@code < 1.0} decrease it.
     * @return A contrast adjustment {@link ColorFilter}.
     */
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

    /**
     * Creates a filter that applies a color tint to the input.
     *
     * @param tint      The {@link Color} to apply as a tint.
     * @param intensity The strength of the tint (0.0 to 1.0).
     * @return A tinting {@link ColorFilter}.
     */
    static ColorFilter tint(Color tint, double intensity) {
        return c -> ColorUtils.mix(c, tint, intensity);
    }

    /**
     * Creates a filter that simulates specific types of color blindness.
     *
     * @param type The {@link ColorBlindness} simulation type to apply.
     * @return A simulation {@link ColorFilter}.
     */
    static ColorFilter colorBlindness(ColorBlindness type) {
        return type::simulate;
    }
}