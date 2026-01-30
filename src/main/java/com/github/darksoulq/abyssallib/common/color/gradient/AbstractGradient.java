package com.github.darksoulq.abyssallib.common.color.gradient;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import com.github.darksoulq.abyssallib.common.color.ColorUtils;
import org.bukkit.Color;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

/**
 * An abstract base class for multi-stop color gradients.
 * <p>
 * This class handles the core logic of interpolating between multiple colors
 * at specific floating-point positions (0.0 to 1.0).
 */
public abstract class AbstractGradient implements ColorProvider {
    /** The array of colors used as gradient stops. */
    protected final Color[] colors;
    /** The corresponding normalized positions (0.0 to 1.0) for each color stop. */
    protected final float[] positions;

    /**
     * Constructs a gradient with specific color stops and their positions.
     *
     * @param colors    The array of {@link Color} stops.
     * @param positions The array of normalized positions (0.0 to 1.0).
     * @throws IllegalArgumentException if the arrays are not of equal length.
     */
    public AbstractGradient(Color[] colors, float[] positions) {
        if (colors.length != positions.length) throw new IllegalArgumentException("Color and position arrays must be same length");
        this.colors = colors;
        this.positions = positions;
    }

    /**
     * Constructs a gradient with colors evenly distributed across the 0.0 to 1.0 range.
     *
     * @param colors The varargs array of {@link Color} stops.
     */
    public AbstractGradient(Color... colors) {
        this.colors = colors;
        this.positions = new float[colors.length];
        if (colors.length == 1) {
            positions[0] = 0f;
        } else {
            for (int i = 0; i < colors.length; i++) {
                positions[i] = i / (float) (colors.length - 1);
            }
        }
    }

    /**
     * Constructs a gradient with a list of colors evenly distributed across the 0.0 to 1.0 range.
     *
     * @param colors The list of {@link Color} stops.
     */
    public AbstractGradient(List<Color> colors) {
        this(colors.toArray(new Color[0]));
    }

    /**
     * Calculates the color at a specific interpolation point.
     *
     * @param t The normalized progress (0.0 to 1.0).
     * @return The interpolated {@link Color}.
     */
    public Color getAt(double t) {
        if (colors.length == 0) return Color.WHITE;
        if (colors.length == 1) return colors[0];

        t = Math.max(0, Math.min(1, t));

        int index = 0;
        for (int i = 0; i < positions.length - 1; i++) {
            if (t >= positions[i] && t <= positions[i + 1]) {
                index = i;
                break;
            }
        }

        float startPos = positions[index];
        float endPos = positions[index + 1];
        double localProgress = (t - startPos) / (endPos - startPos);

        return ColorUtils.mix(colors[index], colors[index + 1], localProgress);
    }

    /**
     * Retrieves the color for a specific position and progress.
     *
     * @param pos      The spatial {@link Vector} position.
     * @param t        The global progress.
     * @return The resulting {@link Color}.
     */
    @Override
    public Color get(Vector pos, double t) {
        return getAt(t);
    }

    /** @return A copy of the color stops array. */
    public Color[] getColors() {
        return Arrays.copyOf(colors, colors.length);
    }

    /** @return A copy of the normalized positions array. */
    public float[] getPositions() {
        return Arrays.copyOf(positions, positions.length);
    }
}