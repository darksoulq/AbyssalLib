package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Represents a collection of {@link Color} objects, providing utility methods for
 * color selection, palette generation, and mathematical analysis.
 * <p>
 * This class is useful for managing specific aesthetic themes, generating gradients,
 * or sampling colors from external assets like images.
 */
public class ColorPalette implements Iterable<Color> {

    /** A predefined palette containing soft, high-lightness pastel colors. */
    public static final ColorPalette PASTEL = new ColorPalette(
        Color.fromRGB(255, 179, 186),
        Color.fromRGB(255, 223, 186),
        Color.fromRGB(255, 255, 186),
        Color.fromRGB(186, 255, 201),
        Color.fromRGB(186, 225, 255)
    );

    /** A predefined palette containing high-saturation, vibrant neon colors. */
    public static final ColorPalette NEON = new ColorPalette(
        Color.fromRGB(255, 0, 255),
        Color.fromRGB(0, 255, 255),
        Color.fromRGB(255, 255, 0),
        Color.fromRGB(57, 255, 20)
    );

    /** A predefined palette representing the standard colors of a rainbow. */
    public static final ColorPalette RAINBOW = new ColorPalette(
        Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE
    );

    /** The internal list of colors comprising this palette. */
    private final List<Color> colors;

    /** Internal random generator for the {@link #random()} method. */
    private final Random random = new Random();

    /**
     * Constructs a palette from a variable number of Color arguments.
     *
     * @param colors The {@link Color} objects to include.
     */
    public ColorPalette(Color... colors) {
        this.colors = new ArrayList<>(Arrays.asList(colors));
    }

    /**
     * Constructs a palette from an existing collection of colors.
     *
     * @param colors A {@link Collection} of {@link Color} objects.
     */
    public ColorPalette(Collection<Color> colors) {
        this.colors = new ArrayList<>(colors);
    }

    /**
     * Creates a palette by sampling colors from a {@link BufferedImage}.
     *
     * @param image     The source image to analyze.
     * @param maxColors The maximum number of unique colors to extract.
     * @return A new {@link ColorPalette} based on the image's colors.
     */
    public static ColorPalette fromImage(BufferedImage image, int maxColors) {
        return new ColorPalette(ColorUtils.palette(image, maxColors));
    }

    /**
     * Generates a gradient palette between two colors.
     *
     * @param start The starting {@link Color}.
     * @param end   The ending {@link Color}.
     * @param steps The number of colors to generate in the transition.
     * @return A new {@link ColorPalette} representing the gradient.
     */
    public static ColorPalette generate(Color start, Color end, int steps) {
        return new ColorPalette(ColorUtils.gradient(start, end, steps));
    }

    /**
     * Retrieves a color by its index. This method wraps around using modulo
     * arithmetic if the index exceeds the palette size.
     *
     * @param index The index to retrieve.
     * @return The {@link Color} at the calculated index.
     */
    public Color get(int index) {
        return colors.get(index % colors.size());
    }

    /**
     * Selects a random color from the palette.
     *
     * @return A random {@link Color} from this palette, or {@link Color#WHITE} if empty.
     */
    public Color random() {
        if (colors.isEmpty()) return Color.WHITE;
        return colors.get(random.nextInt(colors.size()));
    }

    /**
     * Finds the color in this palette that is mathematically closest to the target.
     *
     * @param target The color to compare against.
     * @return The closest {@link Color} found in this palette.
     */
    public Color closest(Color target) {
        return ColorUtils.closest(target, colors);
    }

    /**
     * Calculates the mathematical average of all colors in this palette.
     *
     * @return The averaged {@link Color}.
     */
    public Color average() {
        return ColorUtils.average(colors);
    }

    /**
     * Returns a new ColorPalette sorted by the provided comparator.
     *
     * @param comparator The {@link Comparator} used to define color order.
     * @return A new sorted {@link ColorPalette}.
     */
    public ColorPalette sort(Comparator<Color> comparator) {
        List<Color> sorted = new ArrayList<>(colors);
        sorted.sort(comparator);
        return new ColorPalette(sorted);
    }

    /**
     * Converts this palette into a {@link ColorProvider} for procedural color retrieval.
     *
     * @return A {@link ColorProvider} configured with this palette's colors.
     */
    public ColorProvider toProvider() {
        return ColorProvider.linear(colors);
    }

    /**
     * Returns an iterator over the colors in this palette.
     *
     * @return An {@link Iterator}.
     */
    @Override
    public @NotNull Iterator<Color> iterator() {
        return colors.iterator();
    }
}