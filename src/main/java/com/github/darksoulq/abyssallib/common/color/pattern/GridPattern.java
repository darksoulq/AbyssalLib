package com.github.darksoulq.abyssallib.common.color.pattern;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;

/**
 * A procedural color provider that generates a 2D grid pattern on the XZ plane.
 * <p>
 * This pattern consists of a background color and thin lines that repeat at a set interval.
 */
public class GridPattern implements ColorProvider {
    /** The color of the grid cells. */
    private final Color bg;
    /** The color of the grid lines. */
    private final Color line;
    /** The distance between each grid line. */
    private final double size;
    /** The width of the grid lines. */
    private final double thickness;

    /**
     * Constructs a new GridPattern.
     *
     * @param bg        The background {@link Color}.
     * @param line      The {@link Color} of the grid lines.
     * @param size      The spacing between lines.
     * @param thickness The thickness of the lines.
     */
    public GridPattern(Color bg, Color line, double size, double thickness) {
        this.bg = bg;
        this.line = line;
        this.size = size;
        this.thickness = thickness;
    }

    /**
     * Calculates the grid color for the given position.
     *
     * @param pos      The spatial {@link Vector} position.
     * @param progress The interpolation progress (unused).
     * @return {@link #line} if the position falls within a line's thickness, otherwise {@link #bg}.
     */
    @Override
    public Color get(Vector pos, double progress) {
        double x = Math.abs(pos.getX()) % size;
        double z = Math.abs(pos.getZ()) % size;

        if (x < thickness || z < thickness) return line;
        return bg;
    }
}