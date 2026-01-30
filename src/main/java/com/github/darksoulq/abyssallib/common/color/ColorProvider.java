package com.github.darksoulq.abyssallib.common.color;

import com.github.darksoulq.abyssallib.common.color.gradient.*;
import com.github.darksoulq.abyssallib.common.color.pattern.*;
import org.bukkit.Color;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * A functional interface representing a procedural color source.
 * <p>
 * A ColorProvider determines a {@link Color} based on a 3D spatial position
 * and a temporal progress value. It serves as the base for all gradients,
 * procedural patterns, and dynamic color animations.
 */
@FunctionalInterface
public interface ColorProvider {

    /**
     * Samples a color at the specified 3D position and animation progress.
     *
     * @param position The spatial {@link Vector} representing where to sample the color.
     * @param progress A normalized value (usually 0.0 to 1.0) representing time or completion.
     * @return The resulting {@link Color}.
     */
    Color get(Vector position, double progress);

    /**
     * Samples a color based purely on progress, using a default vector.
     *
     * @param progress The normalized progress value.
     * @return The resulting {@link Color}.
     */
    default Color get(double progress) {
        return get(new Vector(progress, 0, 0), progress);
    }

    /**
     * Wraps this provider with a {@link ColorFilter}.
     *
     * @param filter The filter to apply to the output of this provider.
     * @return A new {@link ColorProvider} that returns filtered colors.
     */
    default ColorProvider filter(ColorFilter filter) {
        return (v, t) -> filter.apply(this.get(v, t));
    }

    /**
     * Creates a provider that always returns the same static color.
     *
     * @param color The {@link Color} to return.
     * @return A fixed color provider.
     */
    static ColorProvider fixed(Color color) {
        return (v, t) -> color;
    }

    /**
     * Creates a dynamic rainbow provider that cycles through hues over the progress value.
     *
     * @return A rainbow {@link ColorProvider}.
     */
    static ColorProvider rainbow() {
        return (v, t) -> ColorUtils.hsb((float) t, 1f, 1f);
    }

    /**
     * Creates a linear gradient provider.
     *
     * @param colors The color stops for the gradient.
     * @return A {@link LinearGradient} instance.
     */
    static ColorProvider linear(Color... colors) {
        return new LinearGradient(colors);
    }

    /**
     * Creates a linear gradient provider from a list.
     *
     * @param colors The list of color stops.
     * @return A {@link LinearGradient} instance.
     */
    static ColorProvider linear(List<Color> colors) {
        return new LinearGradient(colors.toArray(new Color[0]));
    }

    /**
     * Creates a radial gradient provider originating from the center.
     *
     * @param radius The radius at which the gradient completes.
     * @param colors The color stops for the gradient.
     * @return A {@link RadialGradient} instance.
     */
    static ColorProvider radial(double radius, Color... colors) {
        return new RadialGradient(radius, colors);
    }

    /**
     * Creates a sweep gradient provider that rotates around the center.
     *
     * @param colors The color stops for the gradient.
     * @return A {@link SweepGradient} instance.
     */
    static ColorProvider sweep(Color... colors) {
        return new SweepGradient(colors);
    }

    /**
     * Creates a bilinear gradient provider for 2D surface interpolation.
     *
     * @param c00    Color at (0,0).
     * @param c10    Color at (1,0).
     * @param c01    Color at (0,1).
     * @param c11    Color at (1,1).
     * @param scaleX The width of the gradient cycle.
     * @param scaleZ The depth of the gradient cycle.
     * @return A {@link BilinearGradient} instance.
     */
    static ColorProvider bilinear(Color c00, Color c10, Color c01, Color c11, double scaleX, double scaleZ) {
        return new BilinearGradient(c00, c10, c01, c11, scaleX, scaleZ);
    }

    /**
     * Wraps a provider with Perlin noise to create organic, smoky transitions.
     *
     * @param base The base provider to sample from.
     * @param freq The frequency of the noise.
     * @return A {@link NoiseGradient} instance.
     */
    static ColorProvider noise(ColorProvider base, double freq) {
        return new NoiseGradient(base, freq);
    }

    /**
     * Modulates a provider with a sine wave based on position and time.
     *
     * @param base       The base provider.
     * @param dir        The direction of the wave propagation.
     * @param waveLength The distance between wave peaks.
     * @param speed      The rate of wave movement.
     * @return A {@link WaveGradient} instance.
     */
    static ColorProvider wave(ColorProvider base, Vector dir, double waveLength, double speed) {
        return new WaveGradient(base, dir, waveLength, speed);
    }

    /**
     * Creates a 3D checkerboard pattern.
     *
     * @param c1   The first color.
     * @param c2   The second color.
     * @param size The size of each square.
     * @return A {@link CheckeredPattern} instance.
     */
    static ColorProvider checkered(Color c1, Color c2, double size) {
        return new CheckeredPattern(c1, c2, size);
    }

    /**
     * Creates a striped pattern along a specific direction.
     *
     * @param c1    The first stripe color.
     * @param c2    The second stripe color.
     * @param width The width of the stripes.
     * @param dir   The direction of the stripes.
     * @return A {@link StripedPattern} instance.
     */
    static ColorProvider striped(Color c1, Color c2, double width, Vector dir) {
        return new StripedPattern(c1, c2, width, dir);
    }

    /**
     * Creates a grid pattern.
     *
     * @param bg        The background color.
     * @param line      The color of the grid lines.
     * @param size      The spacing between lines.
     * @param thickness The thickness of the lines.
     * @return A {@link GridPattern} instance.
     */
    static ColorProvider grid(Color bg, Color line, double size, double thickness) {
        return new GridPattern(bg, line, size, thickness);
    }

    /**
     * Creates a cellular Voronoi pattern.
     *
     * @param palette The colors available for cells.
     * @param density The number of seed points.
     * @param scale   The coordinate scale.
     * @return A {@link VoronoiPattern} instance.
     */
    static ColorProvider voronoi(Color[] palette, int density, double scale) {
        return new VoronoiPattern(palette, density, scale);
    }
}