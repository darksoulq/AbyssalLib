package com.github.darksoulq.abyssallib.common.color.pattern;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;

/**
 * A procedural color provider that generates a 3D checkerboard pattern.
 * <p>
 * The pattern alternates between two colors based on the floor of the coordinates
 * divided by the specified size.
 */
public class CheckeredPattern implements ColorProvider {
    /** The first color of the checkerboard. */
    private final Color c1;
    /** The second color of the checkerboard. */
    private final Color c2;
    /** The size of each individual color cube in the grid. */
    private final double size;

    /**
     * Constructs a new CheckeredPattern.
     *
     * @param c1   The first {@link Color}.
     * @param c2   The second {@link Color}.
     * @param size The edge length of each checker square.
     */
    public CheckeredPattern(Color c1, Color c2, double size) {
        this.c1 = c1;
        this.c2 = c2;
        this.size = size;
    }

    /**
     * Calculates the color for a specific position in 3D space.
     *
     * @param pos      The spatial {@link Vector} position.
     * @param progress The interpolation progress (unused in this pattern).
     * @return {@link #c1} if the sum of the parity of the grid coordinates is even, otherwise {@link #c2}.
     */
    @Override
    public Color get(Vector pos, double progress) {
        int x = (int) Math.floor(pos.getX() / size);
        int y = (int) Math.floor(pos.getY() / size);
        int z = (int) Math.floor(pos.getZ() / size);
        return ((x + y + z) % 2 == 0) ? c1 : c2;
    }
}