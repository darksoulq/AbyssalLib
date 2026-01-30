package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

/**
 * A utility class for generating various color harmony palettes based on color theory.
 * <p>
 * This class provides methods to create sets of colors that work well together,
 * such as complementary, triadic, and analogous schemes, as well as tonal variations.
 */
public final class ColorHarmony {

    /**
     * Generates a monochromatic palette based on the given color.
     * <p>
     * Monochromatic schemes are derived from a single base hue and extended using
     * its shades, tones, and tints.
     *
     * @param base The base {@link Color} to use as the root of the palette.
     * @return A {@link ColorPalette} containing monochromatic variations.
     */
    public static ColorPalette monochromatic(Color base) {
        return new ColorPalette(ColorUtils.monochromatic(base));
    }

    /**
     * Generates an analogous palette based on the given color.
     * <p>
     * Analogous colors are groups of colors that are adjacent to each other on the
     * color wheel, typically creating serene and comfortable designs.
     *
     * @param base The base {@link Color} to use.
     * @return A {@link ColorPalette} containing the base and its neighbors.
     */
    public static ColorPalette analogous(Color base) {
        return new ColorPalette(ColorUtils.analogous(base));
    }

    /**
     * Generates a complementary palette consisting of the base color and its opposite.
     * <p>
     * Complementary colors provide high contrast and high impact when used together.
     *
     * @param base The base {@link Color} to use.
     * @return A {@link ColorPalette} containing the base and its complement.
     */
    public static ColorPalette complementary(Color base) {
        return new ColorPalette(base, ColorUtils.complementary(base));
    }

    /**
     * Generates a split-complementary palette.
     * <p>
     * This uses a base color and the two colors adjacent to its complement.
     * It provides high contrast but with less tension than a standard complementary scheme.
     *
     * @param base The base {@link Color} to use.
     * @return A {@link ColorPalette} containing split-complementary colors.
     */
    public static ColorPalette splitComplementary(Color base) {
        return new ColorPalette(ColorUtils.splitComplementary(base));
    }

    /**
     * Generates a triadic palette.
     * <p>
     * Triadic harmonies use three colors that are evenly spaced (120 degrees apart)
     * around the color wheel, tending to be quite vibrant.
     *
     * @param base The base {@link Color} to use.
     * @return A {@link ColorPalette} containing triadic variations.
     */
    public static ColorPalette triadic(Color base) {
        return new ColorPalette(ColorUtils.triadic(base));
    }

    /**
     * Generates a tetradic (double-complementary) palette.
     * <p>
     * This scheme uses four colors arranged into two complementary pairs,
     * offering plenty of possibilities for variation.
     *
     * @param base The base {@link Color} to use.
     * @return A {@link ColorPalette} containing tetradic variations.
     */
    public static ColorPalette tetradic(Color base) {
        return new ColorPalette(ColorUtils.tetradic(base));
    }

    /**
     * Generates a palette of shades by transitioning the base color toward black.
     *
     *
     *
     * @param base  The base {@link Color} to shade.
     * @param steps The number of steps (colors) in the resulting palette.
     * @return A {@link ColorPalette} representing the transition to black.
     */
    public static ColorPalette shades(Color base, int steps) {
        Color black = Color.BLACK;
        return new ColorPalette(ColorUtils.gradient(base, black, steps));
    }

    /**
     * Generates a palette of tints by transitioning the base color toward white.
     *
     * @param base  The base {@link Color} to tint.
     * @param steps The number of steps (colors) in the resulting palette.
     * @return A {@link ColorPalette} representing the transition to white.
     */
    public static ColorPalette tints(Color base, int steps) {
        Color white = Color.WHITE;
        return new ColorPalette(ColorUtils.gradient(base, white, steps));
    }

    /**
     * Generates a palette of tones by transitioning the base color toward gray.
     *
     * @param base  The base {@link Color} to tone.
     * @param steps The number of steps (colors) in the resulting palette.
     * @return A {@link ColorPalette} representing the transition to gray.
     */
    public static ColorPalette tones(Color base, int steps) {
        Color gray = Color.GRAY;
        return new ColorPalette(ColorUtils.gradient(base, gray, steps));
    }
}