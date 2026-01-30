package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

/**
 * An enumeration representing various types of color vision deficiencies (CVD).
 * <p>
 * This class provides a mathematical simulation of how different colors are perceived
 * by individuals with specific types of color blindness. The simulation process involves:
 * <ol>
 * <li>Linearizing the sRGB input (Gamma expansion).</li>
 * <li>Applying a transformation matrix based on the deficiency type.</li>
 * <li>De-linearizing the result back to sRGB (Gamma compression).</li>
 * </ol>
 */
public enum ColorBlindness {
    /** Standard human vision with no color deficiency. */
    NORMAL,

    /** * Red-blindness; the inability to perceive red light.
     * Resulting vision consists primarily of blues and yellows.
     */
    PROTANOPIA,

    /** * Green-blindness; the inability to perceive green light.
     * This is the most common form of color blindness.
     */
    DEUTERANOPIA,

    /** * Blue-blindness; the inability to perceive blue light.
     * A rare condition where colors are mostly seen as reds and greens.
     */
    TRITANOPIA,

    /** * Total color blindness; the inability to perceive any color.
     * Vision is restricted to shades of gray (luminance only).
     */
    ACHROMATOPSIA;

    /**
     * Simulates the current color vision deficiency on a given Color.
     * <p>
     * The method performs gamma correction before and after the color space
     * transformation to ensure accuracy in the linear RGB space.
     * </p>
     *
     * @param c The original {@link Color} to be transformed.
     * @return A new {@link Color} representing how the input would be perceived.
     */
    public Color simulate(Color c) {
        if (this == NORMAL) return c;

        double r = c.getRed() / 255.0;
        double g = c.getGreen() / 255.0;
        double b = c.getBlue() / 255.0;

        r = (r <= 0.04045) ? r / 12.92 : Math.pow((r + 0.055) / 1.055, 2.4);
        g = (g <= 0.04045) ? g / 12.92 : Math.pow((g + 0.055) / 1.055, 2.4);
        b = (b <= 0.04045) ? b / 12.92 : Math.pow((b + 0.055) / 1.055, 2.4);

        double l = 0, m = 0, s = 0;

        switch (this) {
            case PROTANOPIA -> {
                l = 0.56667 * r + 0.43333 * g + 0.0 * b;
                m = 0.55833 * r + 0.44167 * g + 0.0 * b;
                s = 0.0 * r + 0.24167 * g + 0.75833 * b;
            }
            case DEUTERANOPIA -> {
                l = 0.625 * r + 0.375 * g + 0.0 * b;
                m = 0.7 * r + 0.3 * g + 0.0 * b;
                s = 0.0 * r + 0.3 * g + 0.7 * b;
            }
            case TRITANOPIA -> {
                l = 0.95 * r + 0.05 * g + 0.0 * b;
                m = 0.0 * r + 0.43333 * g + 0.56667 * b;
                s = 0.0 * r + 0.475 * g + 0.525 * b;
            }
            case ACHROMATOPSIA -> {
                double grey = 0.299 * r + 0.587 * g + 0.114 * b;
                l = m = s = grey;
            }
        }

        l = (l <= 0.0031308) ? 12.92 * l : 1.055 * Math.pow(l, 1 / 2.4) - 0.055;
        m = (m <= 0.0031308) ? 12.92 * m : 1.055 * Math.pow(m, 1 / 2.4) - 0.055;
        s = (s <= 0.0031308) ? 12.92 * s : 1.055 * Math.pow(s, 1 / 2.4) - 0.055;

        return Color.fromRGB(
            clamp((int) (l * 255)),
            clamp((int) (m * 255)),
            clamp((int) (s * 255))
        );
    }

    /**
     * Clamps an integer value between 0 and 255 to ensure it remains within
     * the valid range for RGB components.
     *
     * @param v The value to clamp.
     * @return An integer between 0 and 255 inclusive.
     */
    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}