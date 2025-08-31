package com.github.darksoulq.abyssallib.world.particle;

import org.bukkit.Location;

import java.util.List;
import java.util.Random;

public final class Shapes {

    private static final Random RANDOM = new Random();

    private Shapes() {}

    public static Shape circle(double radius, int points) {
        return (origin, tick, particles) -> {
            List<Location> list = particles.getLocationBuffer(points);
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                list.set(i, particles.poolLocation(origin.getX() + x, origin.getY(), origin.getZ() + z));
            }
            return list;
        };
    }

    public static Shape sphere(double radius, int points) {
        return (origin, tick, particles) -> {
            List<Location> list = particles.getLocationBuffer(points);
            for (int i = 0; i < points; i++) {
                double theta = 2 * Math.PI * RANDOM.nextDouble();
                double phi = Math.acos(2 * RANDOM.nextDouble() - 1);
                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.sin(phi) * Math.sin(theta);
                double z = radius * Math.cos(phi);
                list.set(i, particles.poolLocation(origin.getX() + x, origin.getY() + y, origin.getZ() + z));
            }
            return list;
        };
    }

    public static Shape cubeEdges(double size, int pointsPerEdge) {
        return (origin, tick, particles) -> {
            int totalPoints = pointsPerEdge * 12;
            List<Location> list = particles.getLocationBuffer(totalPoints);
            double half = size / 2;
            int idx = 0;
            double[] xs = {-half, half};
            double[] ys = {-half, half};
            double[] zs = {-half, half};

            for (double x : xs)
                for (double y : ys)
                    for (int i = 0; i < pointsPerEdge; i++)
                        list.set(idx++, particles.poolLocation(origin.getX() + x, origin.getY() + y, origin.getZ() - half + i * (size / (pointsPerEdge - 1))));

            for (double x : xs)
                for (double z : zs)
                    for (int i = 0; i < pointsPerEdge; i++)
                        list.set(idx++, particles.poolLocation(origin.getX() + x, origin.getY() - half + i * (size / (pointsPerEdge - 1)), origin.getZ() + z));

            for (double y : ys)
                for (double z : zs)
                    for (int i = 0; i < pointsPerEdge; i++)
                        list.set(idx++, particles.poolLocation(origin.getX() - half + i * (size / (pointsPerEdge - 1)), origin.getY() + y, origin.getZ() + z));

            return list;
        };
    }

    public static Shape pyramid(double height, int points) {
        return (origin, tick, particles) -> {
            List<Location> list = particles.getLocationBuffer(points);
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                double x = Math.cos(angle) * height;
                double z = Math.sin(angle) * height;
                list.set(i, particles.poolLocation(origin.getX() + x, origin.getY(), origin.getZ() + z + height));
            }
            return list;
        };
    }
}
