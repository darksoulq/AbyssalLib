package com.github.darksoulq.abyssallib.common.color.pattern;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;

import java.util.Random;

public class VoronoiPattern implements ColorProvider {
    private final Color[] palette;
    private final Vector[] points;
    private final double scale;

    public VoronoiPattern(Color[] palette, int density, double scale) {
        this.palette = palette;
        this.scale = scale;
        this.points = new Vector[density];
        Random r = new Random(12345);
        for (int i = 0; i < density; i++) {
            points[i] = new Vector(r.nextDouble() * scale, 0, r.nextDouble() * scale);
        }
    }

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