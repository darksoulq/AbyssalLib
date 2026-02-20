package com.github.darksoulq.abyssallib.common.color.gradient;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import com.github.darksoulq.abyssallib.common.color.ColorUtils;
import org.bukkit.Color;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

/**
 * An abstract base class for multi-stop color gradients.
 * * This class handles the core logic of interpolating between multiple colors
 * at specific floating-point positions within a normalized range of 0.0 to 1.0.
 * It implements the {@link ColorProvider} interface to allow spatial color sampling.
 */
public abstract class AbstractGradient implements ColorProvider {

    /**
     * The array of colors used as gradient stops.
     */
    protected final Color[] colors;

    /**
     * The corresponding normalized positions (0.0 to 1.0) for each color stop.
     */
    protected final float[] positions;

    /**
     * Constructs a gradient with specific color stops and their positions.
     *
     * @param colors
     * The array of {@link Color} stops used for interpolation.
     * @param positions
     * The array of normalized positions (0.0 to 1.0) for each stop.
     * @throws IllegalArgumentException
     * If the color and position arrays do not have the same length.
     */
    public AbstractGradient(Color[] colors, float[] positions) {
        if (colors.length != positions.length) {
            throw new IllegalArgumentException("Color and position arrays must be same length");
        }
        this.colors = colors;
        this.positions = positions;
    }

    /**
     * Constructs a gradient with colors evenly distributed across the 0.0 to 1.0 range.
     *
     * @param colors
     * The varargs array of {@link Color} stops to distribute.
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
     * @param colors
     * The list of {@link Color} stops to distribute.
     */
    public AbstractGradient(List<Color> colors) {
        this(colors.toArray(new Color[0]));
    }

    /**
     * Calculates the color at a specific interpolation point using a local progress calculation.
     *
     * @param t
     * The normalized progress (0.0 to 1.0).
     * @return The interpolated {@link Color} based on the surrounding stops.
     */
    public Color getAt(double t) {
        if (colors.length == 0) {
            return Color.WHITE;
        }
        if (colors.length == 1) {
            return colors[0];
        }

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
     * Retrieves the color for a specific spatial position and time progress.
     *
     * @param pos
     * The spatial {@link Vector} position of the pixel or point.
     * @param t
     * The global progress (typically 0.0 to 1.0).
     * @return The resulting {@link Color} at the sampled point.
     */
    @Override
    public Color get(Vector pos, double t) {
        return getAt(t);
    }

    /**
     * Retrieves a copy of the color stops used in this gradient.
     *
     * @return A new {@link Color} array containing the stops.
     */
    public Color[] getColors() {
        return Arrays.copyOf(colors, colors.length);
    }

    /**
     * Retrieves a copy of the normalized position stops used in this gradient.
     *
     * @return A new float array containing the normalized positions.
     */
    public float[] getPositions() {
        return Arrays.copyOf(positions, positions.length);
    }
}