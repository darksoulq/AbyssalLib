package com.github.darksoulq.abyssallib.common.color.pattern;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * A procedural color provider that generates a Voronoi (cellular) diagram on the XZ plane.
 * <p>
 * The space is divided into cells based on the nearest seed point, where each cell
 * is assigned a color from the provided palette.
 */
public class VoronoiPattern implements ColorProvider {
    /** The palette of colors used to color individual cells. */
    private final Color[] palette;
    /** The array of seed points for the Voronoi cells. */
    private final Vector[] points;
    /** The scale of the coordinate system before repeating. */
    private final double scale;

    /**
     * Constructs a new VoronoiPattern.
     *
     * @param palette The array of {@link Color}s to use.
     * @param density The number of seed points to generate.
     * @param scale   The bounding scale for the generated points.
     */
    public VoronoiPattern(Color[] palette, int density, double scale) {
        this.palette = palette;
        this.scale = scale;
        this.points = new Vector[density];
        Random r = new Random(12345);
        for (int i = 0; i < density; i++) {
            points[i] = new Vector(r.nextDouble() * scale, 0, r.nextDouble() * scale);
        }
    }

    /**
     * Calculates the color of the cell belonging to the closest seed point.
     *
     * @param pos      The spatial {@link Vector} position.
     * @param progress The interpolation progress (unused).
     * @return A {@link Color} from the palette corresponding to the nearest cell.
     */
    @Override
    public Color get(Vector pos, double progress) {
        double x = Math.abs(pos.getX()) % scale;
        double z = Math.abs(pos.getZ()) % scale;

        int closest = 0;
        double minDst = Double.MAX_VALUE;

        for (int i = 0; i < points.length; i++) {
            double d = Math.pow(x - points[i].getX(), 2) + Math.pow(z - points[i].getZ(), 2);
            if (d < minDst) {
                minDst = d;
                closest = i;
            }
        }
        return palette[closest % palette.length];
    }
}