package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.*;

public class ColorPalette implements Iterable<Color> {
    public static final ColorPalette PASTEL = new ColorPalette(
        Color.fromRGB(255, 179, 186),
        Color.fromRGB(255, 223, 186),
        Color.fromRGB(255, 255, 186),
        Color.fromRGB(186, 255, 201),
        Color.fromRGB(186, 225, 255)
    );
    public static final ColorPalette NEON = new ColorPalette(
        Color.fromRGB(255, 0, 255),
        Color.fromRGB(0, 255, 255),
        Color.fromRGB(255, 255, 0),
        Color.fromRGB(57, 255, 20)
    );
    public static final ColorPalette RAINBOW = new ColorPalette(
        Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE
    );

    private final List<Color> colors;
    private final Random random = new Random();

    public ColorPalette(Color... colors) {
        this.colors = new ArrayList<>(Arrays.asList(colors));
    }

    public ColorPalette(Collection<Color> colors) {
        this.colors = new ArrayList<>(colors);
    }

    public static ColorPalette fromImage(BufferedImage image, int maxColors) {
        return new ColorPalette(ColorUtils.palette(image, maxColors));
    }

    public static ColorPalette generate(Color start, Color end, int steps) {
        return new ColorPalette(ColorUtils.gradient(start, end, steps));
    }

    public Color get(int index) {
        return colors.get(index % colors.size());
    }

    public Color random() {
        if (colors.isEmpty()) return Color.WHITE;
        return colors.get(random.nextInt(colors.size()));
    }

    public Color closest(Color target) {
        return ColorUtils.closest(target, colors);
    }

    public Color average() {
        return ColorUtils.average(colors);
    }

    public ColorPalette sort(Comparator<Color> comparator) {
        List<Color> sorted = new ArrayList<>(colors);
        sorted.sort(comparator);
        return new ColorPalette(sorted);
    }

    public ColorProvider toProvider() {
        return ColorProvider.linear(colors);
    }

    @Override
    public @NotNull Iterator<Color> iterator() {
        return colors.iterator();
    }
}