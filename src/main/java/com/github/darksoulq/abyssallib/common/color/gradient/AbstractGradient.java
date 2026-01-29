package com.github.darksoulq.abyssallib.common.color.gradient;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import com.github.darksoulq.abyssallib.common.color.ColorUtils;
import org.bukkit.Color;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractGradient implements ColorProvider {
    protected final Color[] colors;
    protected final float[] positions;

    public AbstractGradient(Color[] colors, float[] positions) {
        if (colors.length != positions.length) throw new IllegalArgumentException("Color and position arrays must be same length");
        this.colors = colors;
        this.positions = positions;
    }

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

    public AbstractGradient(List<Color> colors) {
        this(colors.toArray(new Color[0]));
    }

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

    @Override
    public Color get(Vector pos, double t) {
        return getAt(t);
    }

    public Color[] getColors() {
        return Arrays.copyOf(colors, colors.length);
    }

    public float[] getPositions() {
        return Arrays.copyOf(positions, positions.length);
    }
}