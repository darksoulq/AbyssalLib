package com.github.darksoulq.abyssallib.particle;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimatedShapes {
    public static Shape rotatingCircle(double radius, int points, double rotationSpeed) {
        return new Shape() {
            private double angleOffset = 0;

            @Override
            public List<Location> points(Location origin) {
                List<Location> result = new ArrayList<>();
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points + angleOffset;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    result.add(origin.clone().add(x, 0, z));
                }
                return result;
            }

            @Override
            public void animate(Particles builder, Location origin, long tick) {
                angleOffset += rotationSpeed;
                if (angleOffset > 2 * Math.PI) angleOffset -= 2 * Math.PI;
            }
        };
    }

    public static Shape rotatingSphere(double radius, int points, double rotationSpeed) {
        return new Shape() {
            private double angleOffset = 0;

            @Override
            public List<Location> points(Location origin) {
                List<Location> result = new ArrayList<>();
                Random rand = new Random();
                for (int i = 0; i < points; i++) {
                    double theta = 2 * Math.PI * rand.nextDouble();
                    double phi = Math.acos(2 * rand.nextDouble() - 1);
                    double x = radius * Math.sin(phi) * Math.cos(theta + angleOffset);
                    double y = radius * Math.sin(phi) * Math.sin(theta + angleOffset);
                    double z = radius * Math.cos(phi);
                    result.add(origin.clone().add(x, y, z));
                }
                return result;
            }

            @Override
            public void animate(Particles builder, Location origin, long tick) {
                angleOffset += rotationSpeed;
                if (angleOffset > 2 * Math.PI) angleOffset -= 2 * Math.PI;
            }
        };
    }
}
