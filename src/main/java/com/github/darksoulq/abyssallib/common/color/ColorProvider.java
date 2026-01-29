package com.github.darksoulq.abyssallib.common.color;

import com.github.darksoulq.abyssallib.common.color.gradient.*;
import com.github.darksoulq.abyssallib.common.color.pattern.*;
import org.bukkit.Color;
import org.bukkit.util.Vector;

import java.util.List;

@FunctionalInterface
public interface ColorProvider {
    Color get(Vector position, double progress);

    default Color get(double progress) {
        return get(new Vector(progress, 0, 0), progress);
    }

    default ColorProvider filter(ColorFilter filter) {
        return (v, t) -> filter.apply(this.get(v, t));
    }

    static ColorProvider fixed(Color color) {
        return (v, t) -> color;
    }

    static ColorProvider rainbow() {
        return (v, t) -> ColorUtils.hsb((float) t, 1f, 1f);
    }

    static ColorProvider linear(Color... colors) {
        return new LinearGradient(colors);
    }

    static ColorProvider linear(List<Color> colors) {
        return new LinearGradient(colors.toArray(new Color[0]));
    }

    static ColorProvider radial(double radius, Color... colors) {
        return new RadialGradient(radius, colors);
    }

    static ColorProvider sweep(Color... colors) {
        return new SweepGradient(colors);
    }

    static ColorProvider bilinear(Color c00, Color c10, Color c01, Color c11, double scaleX, double scaleZ) {
        return new BilinearGradient(c00, c10, c01, c11, scaleX, scaleZ);
    }

    static ColorProvider noise(ColorProvider base, double freq) {
        return new NoiseGradient(base, freq);
    }

    static ColorProvider wave(ColorProvider base, Vector dir, double waveLength, double speed) {
        return new WaveGradient(base, dir, waveLength, speed);
    }

    static ColorProvider checkered(Color c1, Color c2, double size) {
        return new CheckeredPattern(c1, c2, size);
    }

    static ColorProvider striped(Color c1, Color c2, double width, Vector dir) {
        return new StripedPattern(c1, c2, width, dir);
    }

    static ColorProvider grid(Color bg, Color line, double size, double thickness) {
        return new GridPattern(bg, line, size, thickness);
    }

    static ColorProvider voronoi(Color[] palette, int density, double scale) {
        return new VoronoiPattern(palette, density, scale);
    }
}