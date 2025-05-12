package com.github.darksoulq.abyssallib.particle;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class providing predefined {@link Shape} implementations
 * for use with the {@link Particles} API.
 */
public class Shapes {
    /**
     * Creates a circular shape in the XZ plane.
     *
     * @param radius the radius of the circle
     * @param points the number of points (particle positions) around the circle
     * @return a {@link Shape} representing a flat circle
     */
    public static Shape circle(double radius, int points) {
        return origin -> {
            List<Location> result = new ArrayList<>();
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                result.add(origin.clone().add(x, 0, z));
            }
            return result;
        };
    }

    /**
     * Creates a 3D spherical shape with randomly distributed points.
     *
     * @param radius the radius of the sphere
     * @param points the number of particle points to generate inside the sphere
     * @return a {@link Shape} representing a filled sphere
     */
    public static Shape sphere(double radius, int points) {
        return origin -> {
            List<Location> result = new ArrayList<>();
            Random rand = new Random();
            for (int i = 0; i < points; i++) {
                double theta = 2 * Math.PI * rand.nextDouble();
                double phi = Math.acos(2 * rand.nextDouble() - 1);
                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.sin(phi) * Math.sin(theta);
                double z = radius * Math.cos(phi);
                result.add(origin.clone().add(x, y, z));
            }
            return result;
        };
    }

    /**
     * Creates a cubic shape, with points on the corners of the cube.
     *
     * @param size the length of each edge of the cube
     * @return a {@link Shape} representing a hollow cube outline
     */
    public static Shape cube(double size) {
        return origin -> {
            List<Location> result = new ArrayList<>();
            double half = size / 2;
            for (double x = -half; x <= half; x += size) {
                for (double y = -half; y <= half; y += size) {
                    for (double z = -half; z <= half; z += size) {
                        result.add(origin.clone().add(x, y, z));
                    }
                }
            }
            return result;
        };
    }

    /**
     * Creates a pyramid-like shape with points radiating from the base toward the top.
     *
     * @param height the vertical height of the pyramid
     * @param points number of base points forming the pyramid's footprint
     * @return a {@link Shape} representing a pyramid
     */
    public static Shape pyramid(double height, int points) {
        return origin -> {
            List<Location> result = new ArrayList<>();
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                double x = Math.cos(angle) * height;
                double z = Math.sin(angle) * height;
                result.add(origin.clone().add(x, 0, z).add(0, height, 0));
            }
            return result;
        };
    }
}
