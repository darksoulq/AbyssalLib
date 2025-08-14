package com.github.darksoulq.abyssallib.world.level.particle;

import org.bukkit.Location;

import java.util.List;

public class AnimatedShapes {

    public static Shape rotatingCircle(double radius, int points, double rotationSpeed) {
        return (origin, tick, builder) -> {
            List<Location> buffer = builder.getLocationBuffer(points);
            double angleOffset = (tick * rotationSpeed) % (2 * Math.PI);
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points + angleOffset;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                buffer.set(i, builder.poolLocation(origin.getX() + x, origin.getY(), origin.getZ() + z));
            }
            return buffer;
        };
    }

    public static Shape rotatingSphere(double radius, int points, double rotationSpeed) {
        return (origin, tick, builder) -> {
            List<Location> buffer = builder.getLocationBuffer(points);
            java.util.Random rand = new java.util.Random(tick);
            for (int i = 0; i < points; i++) {
                double theta = 2 * Math.PI * rand.nextDouble();
                double phi = Math.acos(2 * rand.nextDouble() - 1);
                double angleOffset = (tick * rotationSpeed) % (2 * Math.PI);
                double x = radius * Math.sin(phi) * Math.cos(theta + angleOffset);
                double y = radius * Math.sin(phi) * Math.sin(theta + angleOffset);
                double z = radius * Math.cos(phi);
                buffer.set(i, builder.poolLocation(origin.getX() + x, origin.getY() + y, origin.getZ() + z));
            }
            return buffer;
        };
    }

    public static Shape cubeEdges(double size, int pointsPerEdge) {
        int totalPoints = pointsPerEdge * 12;
        return (origin, tick, builder) -> {
            List<Location> buffer = builder.getLocationBuffer(totalPoints);
            double half = size / 2;
            int idx = 0;
            double[] xs = {-half, half};
            double[] ys = {-half, half};
            double[] zs = {-half, half};
            for (double x : xs) for (double y : ys) for (int i = 0; i < pointsPerEdge; i++)
                buffer.set(idx++, builder.poolLocation(origin.getX() + x, origin.getY() + y, origin.getZ() - half + i * (size / (pointsPerEdge - 1))));
            for (double x : xs) for (double z : zs) for (int i = 0; i < pointsPerEdge; i++)
                buffer.set(idx++, builder.poolLocation(origin.getX() + x, origin.getY() - half + i * (size / (pointsPerEdge - 1)), origin.getZ() + z));
            for (double y : ys) for (double z : zs) for (int i = 0; i < pointsPerEdge; i++)
                buffer.set(idx++, builder.poolLocation(origin.getX() - half + i * (size / (pointsPerEdge - 1)), origin.getY() + y, origin.getZ() + z));
            return buffer;
        };
    }

    public static Shape rotatingDiamondCube(double size, int pointsPerEdge, double rotationSpeed) {
        int totalPoints = pointsPerEdge * 12;
        return (origin, tick, builder) -> {
            List<Location> buffer = builder.getLocationBuffer(totalPoints);
            double half = size / 2;
            int idx = 0;
            double[] xs = {-half, half};
            double[] ys = {-half, half};
            double[] zs = {-half, half};
            float angle = (float) (tick * rotationSpeed);
            org.joml.Matrix3f rot = new org.joml.Matrix3f().rotateXYZ(angle, angle, angle);
            for (double x : xs) for (double y : ys) for (int i = 0; i < pointsPerEdge; i++) {
                org.joml.Vector3f v = new org.joml.Vector3f((float) x, (float) y, (float) (-half + i * (size / (pointsPerEdge - 1))));
                rot.transform(v);
                buffer.set(idx++, builder.poolLocation(origin.getX() + v.x, origin.getY() + v.y, origin.getZ() + v.z));
            }
            for (double x : xs) for (double z : zs) for (int i = 0; i < pointsPerEdge; i++) {
                org.joml.Vector3f v = new org.joml.Vector3f((float) x, (float) (-half + i * (size / (pointsPerEdge - 1))), (float) z);
                rot.transform(v);
                buffer.set(idx++, builder.poolLocation(origin.getX() + v.x, origin.getY() + v.y, origin.getZ() + v.z));
            }
            for (double y : ys) for (double z : zs) for (int i = 0; i < pointsPerEdge; i++) {
                org.joml.Vector3f v = new org.joml.Vector3f((float) (-half + i * (size / (pointsPerEdge - 1))), (float) y, (float) z);
                rot.transform(v);
                buffer.set(idx++, builder.poolLocation(origin.getX() + v.x, origin.getY() + v.y, origin.getZ() + v.z));
            }
            return buffer;
        };
    }
}
