package com.github.darksoulq.abyssallib.common.color.gradient;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import com.github.darksoulq.abyssallib.common.color.ColorUtils;
import org.bukkit.Color;
import org.bukkit.util.Vector;

/**
 * A color provider that performs bilinear interpolation between four corner colors on a 2D plane.
 */
public class BilinearGradient implements ColorProvider {
    /** Top-left corner color. */
    private final Color c00;
    /** Top-right corner color. */
    private final Color c10;
    /** Bottom-left corner color. */
    private final Color c01;
    /** Bottom-right corner color. */
    private final Color c11;
    /** The repeating scale of the gradient on the X axis. */
    private final double scaleX;
    /** The repeating scale of the gradient on the Z axis. */
    private final double scaleZ;

    /**
     * Constructs a BilinearGradient.
     *
     * @param c00    Color at (0, 0).
     * @param c10    Color at (1, 0).
     * @param c01    Color at (0, 1).
     * @param c11    Color at (1, 1).
     * @param scaleX The width of the gradient unit.
     * @param scaleZ The length of the gradient unit.
     */
    public BilinearGradient(Color c00, Color c10, Color c01, Color c11, double scaleX, double scaleZ) {
        this.c00 = c00;
        this.c10 = c10;
        this.c01 = c01;
        this.c11 = c11;
        this.scaleX = scaleX;
        this.scaleZ = scaleZ;
    }

    /**
     * Maps the spatial position to a normalized 2D coordinate system and performs bilinear interpolation.
     *
     * @param pos      The spatial {@link Vector} position.
     * @param progress The interpolation progress (unused).
     * @return The interpolated {@link Color}.
     */
    @Override
    public Color get(Vector pos, double progress) {
        double u = (pos.getX() % scaleX) / scaleX;
        double v = (pos.getZ() % scaleZ) / scaleZ;

        if (u < 0) u += 1;
        if (v < 0) v += 1;

        Color top = ColorUtils.mix(c00, c10, u);
        Color bottom = ColorUtils.mix(c01, c11, u);

        return ColorUtils.mix(top, bottom, v);
    }
}