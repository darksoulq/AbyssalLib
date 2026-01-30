package com.github.darksoulq.abyssallib.common.color.pattern;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;

/**
 * A procedural color provider that generates stripes alternating along a specific direction vector.
 */
public class StripedPattern implements ColorProvider {
    /** The first stripe color. */
    private final Color c1;
    /** The second stripe color. */
    private final Color c2;
    /** The width of each individual stripe. */
    private final double width;
    /** The normalized direction vector perpendicular to the stripes. */
    private final Vector direction;

    /**
     * Constructs a new StripedPattern.
     *
     * @param c1        The first {@link Color}.
     * @param c2        The second {@link Color}.
     * @param width     The width of the stripes.
     * @param direction The {@link Vector} direction the stripes should follow.
     */
    public StripedPattern(Color c1, Color c2, double width, Vector direction) {
        this.c1 = c1;
        this.c2 = c2;
        this.width = width;
        this.direction = direction.normalize();
    }

    /**
     * Calculates the color based on the projection of the position onto the direction vector.
     *
     * @param pos      The spatial {@link Vector} position.
     * @param progress The interpolation progress (unused).
     * @return The color corresponding to the current stripe segment.
     */
    @Override
    public Color get(Vector pos, double progress) {
        double proj = pos.dot(direction);
        return (Math.floor(proj / width) % 2 == 0) ? c1 : c2;
    }
}