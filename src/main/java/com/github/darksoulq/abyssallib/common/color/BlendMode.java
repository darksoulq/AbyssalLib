package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

/**
 * An enumeration of standard digital image blending modes.
 * <p>
 * These modes define the mathematical logic used to combine a base (background)
 * color and a source (foreground) color to produce a result color.
 */
public enum BlendMode {

    /**
     * Normal blending mode. The source color completely replaces the base color.
     * <p>Formula: {@code Result = Source}</p>
     */
    NORMAL {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The source color.
         */
        @Override public Color blend(Color b, Color s) { return s; }
    },

    /**
     * Multiplies the base color by the source color. The result is always darker.
     * <p>Formula: {@code Result = (Base * Source) / 255}</p>
     */
    MULTIPLY {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The multiplied color.
         */
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB((b.getRed() * s.getRed()) / 255, (b.getGreen() * s.getGreen()) / 255, (b.getBlue() * s.getBlue()) / 255);
        }
    },

    /**
     * Inverts both colors, multiplies them, and inverts the result. The result is always lighter.
     * <p>Formula: {@code Result = 255 - ((255 - Base) * (255 - Source)) / 255}</p>
     */
    SCREEN {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The screened color.
         */
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(255 - ((255 - b.getRed()) * (255 - s.getRed())) / 255,
                255 - ((255 - b.getGreen()) * (255 - s.getGreen())) / 255,
                255 - ((255 - b.getBlue()) * (255 - s.getBlue())) / 255);
        }
    },

    /**
     * A combination of Multiply and Screen. If the base is light, it uses Screen;
     * if the base is dark, it uses Multiply.
     */
    OVERLAY {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The overlaid color.
         */
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(overlay(b.getRed(), s.getRed()), overlay(b.getGreen(), s.getGreen()), overlay(b.getBlue(), s.getBlue()));
        }

        /**
         * Internal math for the overlay mode per channel.
         * @param b Base channel.
         * @param s Source channel.
         * @return Result channel.
         */
        private int overlay(int b, int s) {
            return b < 128 ? (2 * b * s / 255) : (255 - 2 * (255 - b) * (255 - s) / 255);
        }
    },

    /**
     * Selects the darker of the two colors for each channel.
     * <p>Formula: {@code Result = min(Base, Source)}</p>
     */
    DARKEN {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The darker color.
         */
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(Math.min(b.getRed(), s.getRed()), Math.min(b.getGreen(), s.getGreen()), Math.min(b.getBlue(), s.getBlue()));
        }
    },

    /**
     * Selects the lighter of the two colors for each channel.
     * <p>Formula: {@code Result = max(Base, Source)}</p>
     */
    LIGHTEN {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The lighter color.
         */
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(Math.max(b.getRed(), s.getRed()), Math.max(b.getGreen(), s.getGreen()), Math.max(b.getBlue(), s.getBlue()));
        }
    },

    /**
     * Adds the source color to the base color, clamping at 255.
     * <p>Formula: {@code Result = min(255, Base + Source)}</p>
     */
    ADD {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The sum of the colors.
         */
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(Math.min(255, b.getRed() + s.getRed()), Math.min(255, b.getGreen() + s.getGreen()), Math.min(255, b.getBlue() + s.getBlue()));
        }
    },

    /**
     * Subtracts the source color from the base color, clamping at 0.
     * <p>Formula: {@code Result = max(0, Base - Source)}</p>
     */
    SUBTRACT {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The subtracted color.
         */
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(Math.max(0, b.getRed() - s.getRed()), Math.max(0, b.getGreen() - s.getGreen()), Math.max(0, b.getBlue() - s.getBlue()));
        }
    },

    /**
     * Subtracts the darker color from the lighter color for each channel.
     * <p>Formula: {@code Result = |Base - Source|}</p>
     */
    DIFFERENCE {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The absolute difference of the colors.
         */
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(Math.abs(b.getRed() - s.getRed()), Math.abs(b.getGreen() - s.getGreen()), Math.abs(b.getBlue() - s.getBlue()));
        }
    },

    /**
     * Similar to Difference but lower contrast.
     * <p>Formula: {@code Result = Base + Source - (2 * Base * Source / 255)}</p>
     */
    EXCLUSION {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The exclusion result.
         */
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(
                b.getRed() + s.getRed() - 2 * b.getRed() * s.getRed() / 255,
                b.getGreen() + s.getGreen() - 2 * b.getGreen() * s.getGreen() / 255,
                b.getBlue() + s.getBlue() - 2 * b.getBlue() * s.getBlue() / 255
            );
        }
    },

    /**
     * Calculates the mathematical average of the two colors.
     * <p>Formula: {@code Result = (Base + Source) / 2}</p>
     */
    AVERAGE {
        /**
         * @param b The base color.
         * @param s The source color.
         * @return The average color.
         */
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB((b.getRed() + s.getRed()) / 2, (b.getGreen() + s.getGreen()) / 2, (b.getBlue() + s.getBlue()) / 2);
        }
    };

    /**
     * Blends a source color into a base color using this specific mode's logic.
     *
     * @param base   The background color.
     * @param source The foreground/overlay color.
     * @return The resulting blended {@link Color}.
     */
    public abstract Color blend(Color base, Color source);
}