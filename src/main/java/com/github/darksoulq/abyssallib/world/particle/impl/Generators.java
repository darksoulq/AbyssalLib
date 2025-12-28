package com.github.darksoulq.abyssallib.world.particle.impl;

import com.github.darksoulq.abyssallib.world.particle.Generator;
import com.github.darksoulq.abyssallib.world.particle.style.Pixel;
import org.bukkit.util.Vector;
import org.intellij.lang.annotations.MagicConstant;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class Generators {
    /**
     * Converts a BufferedImage into particle points.
     * @param image The source image
     * @param size The physical size of the resulting shape
     * @param density How many pixels to skip (1 = every pixel, 5 = every 5th). Higher = better performance.
     */
    public static Generator fromImage(BufferedImage image, double size, int density) {
        List<Vector> points = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();

        double scaleX = size / width;
        double scaleY = size / height;
        double offsetX = size / 2.0;
        double offsetY = size / 2.0;

        for (int x = 0; x < width; x += density) {
            for (int y = 0; y < height; y += density) {
                int argb = image.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;
                if (alpha == 0) continue;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = (argb) & 0xFF;
                org.bukkit.Color bukkitColor = org.bukkit.Color.fromRGB(r, g, b);

                double vecX = (x * scaleX) - offsetX;
                double vecY = ((height - y) * scaleY) - offsetY;
                points.add(new Pixel(vecX, vecY, 0, bukkitColor));
            }
        }
        List<Vector> finalPoints = List.copyOf(points);
        return tick -> finalPoints;
    }

    /**
     * Creates text out of particles.
     */
    public static Generator text(String text, String fontName, @MagicConstant(flags = {Font.PLAIN,Font.BOLD,Font.ITALIC}) int style, Color color, int fontSize, double worldSize) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font(fontName, style, fontSize);
        g2d.setFont(font);
        int width = g2d.getFontMetrics().stringWidth(text);
        int height = g2d.getFontMetrics().getHeight();
        g2d.dispose();
        img = new BufferedImage(Math.max(1, width), Math.max(1, height), BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setFont(font);
        g2d.setColor(color);
        g2d.drawString(text, 0, g2d.getFontMetrics().getAscent());
        g2d.dispose();
        return fromImage(img, worldSize, 2);
    }
    public static Generator text(String text, String fontName, @MagicConstant(flags = {Font.PLAIN,Font.BOLD,Font.ITALIC}) int style, int fontSize, double worldSize) {
        return text(text, fontName, style, Color.WHITE, fontSize, worldSize);
    }

    public static Generator point() {
        List<Vector> list = List.of(new Vector(0, 0, 0));
        return tick -> list;
    }
    public static Generator line(Vector start, Vector end, double step) {
        List<Vector> list = new ArrayList<>();
        Vector direction = end.clone().subtract(start);
        double length = direction.length();
        direction.normalize().multiply(step);

        for (double d = 0; d < length; d += step) {
            list.add(start.clone().add(direction.clone().multiply(d)));
        }
        return tick -> list;
    }
    public static Generator circle(double radius, int points) {
        List<Vector> list = new ArrayList<>(points);
        double increment = (2 * Math.PI) / points;
        for (int i = 0; i < points; i++) {
            double angle = i * increment;
            list.add(new Vector(
                Math.cos(angle) * radius,
                0,
                Math.sin(angle) * radius
            ));
        }
        return tick -> list;
    }
    public static Generator square(double size, int pointsPerSide) {
        List<Vector> list = new ArrayList<>();
        double half = size / 2.0;
        double step = size / pointsPerSide;

        for (int i = 0; i <= pointsPerSide; i++) {
            double p = -half + i * step;

            list.add(new Vector(p, 0, -half));
            list.add(new Vector(p, 0, half));
            list.add(new Vector(-half, 0, p));
            list.add(new Vector(half, 0, p));
        }
        return tick -> list;

    }
    public static Generator sphere(double radius, int points) {
        List<Vector> list = new ArrayList<>(points);
        double phi = Math.PI * (3. - Math.sqrt(5.));

        for (int i = 0; i < points; i++) {
            double y = 1 - (i / (double) (points - 1)) * 2;
            double radiusAtY = Math.sqrt(1 - y * y);
            double theta = phi * i;

            double x = Math.cos(theta) * radiusAtY;
            double z = Math.sin(theta) * radiusAtY;

            list.add(new Vector(x, y, z).multiply(radius));
        }
        return tick -> list;
    }
    public static Generator cube(double size, int resolution) {
        List<Vector> list = new ArrayList<>();
        double half = size / 2.0;
        double step = size / resolution;

        for (int x = 0; x <= resolution; x++) {
            for (int y = 0; y <= resolution; y++) {
                for (int z = 0; z <= resolution; z++) {

                    boolean onSurface =
                            x == 0 || x == resolution ||
                            y == 0 || y == resolution ||
                            z == 0 || z == resolution;

                    if (onSurface) {
                        list.add(new Vector(
                                -half + x * step,
                                -half + y * step,
                                -half + z * step
                        ));
                    }
                }
            }
        }
        return tick -> list;
    }
    public static Generator pyramid(double size, double height, int resolution) {
        List<Vector> list = new ArrayList<>();

        for (int y = 0; y <= resolution; y++) {
            double t = y / (double) resolution;
            double layerSize = size * (1 - t);
            double half = layerSize / 2.0;
            double step = layerSize / resolution;

            for (int x = 0; x <= resolution; x++) {
                for (int z = 0; z <= resolution; z++) {
                    list.add(new Vector(
                            -half + x * step,
                            t * height,
                            -half + z * step
                    ));
                }
            }
        }
        return tick -> list;
    }

    public static Generator rotatingCircle(double radius, int points, double speed) {
        List<Vector> base = new ArrayList<>(points);
        double step = Math.PI * 2 / points;

        for (int i = 0; i < points; i++) {
            double a = i * step;
            base.add(new Vector(
                Math.cos(a) * radius,
                0,
                Math.sin(a) * radius
            ));
        }

        return tick -> {
            List<Vector> out = new ArrayList<>(points);
            double rot = tick * speed;

            for (Vector v : base) {
                out.add(v.clone().rotateAroundY(rot));
            }
            return out;
        };
    }
    public static Generator orbit(double radius, int points, double speed) {
        List<Vector> base = new ArrayList<>(points);
        double step = Math.PI * 2 / points;

        for (int i = 0; i < points; i++) {
            double a = i * step;
            base.add(new Vector(
                Math.cos(a) * radius,
                0,
                Math.sin(a) * radius
            ));
        }

        return tick -> {
            List<Vector> out = new ArrayList<>(points);
            double rot = tick * speed;

            for (Vector v : base) {
                out.add(v.clone().rotateAroundY(rot));
            }
            return out;
        };
    }
    public static Generator drawingCircle(double radius, int totalPoints, long duration) {
        List<Vector> base = new ArrayList<>(totalPoints);
        double step = Math.PI * 2 / totalPoints;

        for (int i = 0; i < totalPoints; i++) {
            double a = i * step;
            base.add(new Vector(
                Math.cos(a) * radius,
                0,
                Math.sin(a) * radius
            ));
        }

        return tick -> {
            int visible = duration <= 0
                ? totalPoints
                : (int) Math.min(totalPoints, totalPoints * tick / duration);

            List<Vector> out = new ArrayList<>(visible);
            for (int i = 0; i < visible; i++) {
                out.add(base.get(i));
            }
            return out;
        };
    }
    public static Generator growingCube(double size, int resolution, long duration) {
        List<Vector> base = new ArrayList<>();
        double half = size / 2.0;
        double step = size / resolution;

        for (int y = 0; y <= resolution; y++) {
            for (int x = 0; x <= resolution; x++) {
                for (int z = 0; z <= resolution; z++) {

                    boolean onSurface =
                        x == 0 || x == resolution ||
                            y == 0 || y == resolution ||
                            z == 0 || z == resolution;

                    if (onSurface) {
                        base.add(new Vector(
                            -half + x * step,
                            -half + y * step,
                            -half + z * step
                        ));
                    }
                }
            }
        }

        return tick -> {
            int maxLayer = (int) Math.min(resolution, resolution * tick / Math.max(1, duration));
            double maxY = -half + maxLayer * step;

            List<Vector> out = new ArrayList<>();
            for (Vector v : base) {
                if (v.getY() <= maxY) {
                    out.add(v);
                }
            }
            return out;
        };
    }
    public static Generator sineWave(double length, int points, double amplitude, double frequency, double speed) {
        List<Vector> base = new ArrayList<>(points);
        double step = length / (points - 1);

        for (int i = 0; i < points; i++) {
            double x = -length / 2 + i * step;
            base.add(new Vector(x, 0, 0));
        }

        return tick -> {
            List<Vector> out = new ArrayList<>(points);
            double phase = tick * speed;

            for (Vector v : base) {
                double y = Math.sin(v.getX() * frequency + phase) * amplitude;
                out.add(new Vector(v.getX(), y, 0));
            }
            return out;
        };
    }
    public static Generator pulsingCircle(double baseRadius, double pulseAmplitude, double speed, int points) {
        List<Vector> base = new ArrayList<>(points);
        double step = Math.PI * 2 / points;

        for (int i = 0; i < points; i++) {
            double a = i * step;
            base.add(new Vector(
                Math.cos(a),
                0,
                Math.sin(a)
            ));
        }

        return tick -> {
            double r = baseRadius + Math.sin(tick * speed) * pulseAmplitude;
            List<Vector> out = new ArrayList<>(points);

            for (Vector v : base) {
                out.add(v.clone().multiply(r));
            }
            return out;
        };
    }
    public static Generator helix(double radius, double height, int turns, int pointsPerTurn, double speed) {
        int total = turns * pointsPerTurn;
        List<Vector> base = new ArrayList<>(total);
        double yStep = height / total;
        double angleStep = Math.PI * 2 / pointsPerTurn;

        for (int i = 0; i < total; i++) {
            double a = i * angleStep;
            base.add(new Vector(
                Math.cos(a) * radius,
                i * yStep,
                Math.sin(a) * radius
            ));
        }

        return tick -> {
            List<Vector> out = new ArrayList<>(total);
            double rot = tick * speed;

            for (Vector v : base) {
                out.add(v.clone().rotateAroundY(rot));
            }
            return out;
        };
    }
    public static Generator spiral(double maxRadius, double height, int points, double speed) {
        List<Vector> base = new ArrayList<>(points);

        for (int i = 0; i < points; i++) {
            double t = i / (double) points;
            double r = maxRadius * t;
            double a = t * Math.PI * 2;

            base.add(new Vector(
                Math.cos(a) * r,
                t * height,
                Math.sin(a) * r
            ));
        }

        return tick -> {
            List<Vector> out = new ArrayList<>(points);
            double rot = tick * speed;

            for (Vector v : base) {
                out.add(v.clone().rotateAroundY(rot));
            }
            return out;
        };
    }
}
