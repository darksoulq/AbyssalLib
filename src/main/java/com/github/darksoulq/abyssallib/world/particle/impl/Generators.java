package com.github.darksoulq.abyssallib.world.particle.impl;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import com.github.darksoulq.abyssallib.world.particle.Generator;
import com.github.darksoulq.abyssallib.world.particle.style.Pixel;
import org.bukkit.Color;
import org.bukkit.util.Vector;
import org.intellij.lang.annotations.MagicConstant;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class Generators {

    public static Generator fromImage(BufferedImage image, double size, int density) {
        List<Vector> points = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();
        double scale = Math.min(size / width, size / height);
        double offsetX = (width * scale) / 2.0;
        double offsetY = (height * scale) / 2.0;

        for (int x = 0; x < width; x += density) {
            for (int y = 0; y < height; y += density) {
                int argb = image.getRGB(x, y);
                if ((argb >> 24 & 0xFF) == 0) continue;

                org.bukkit.Color c = org.bukkit.Color.fromRGB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
                points.add(new Pixel(x * scale - offsetX, (height - y) * scale - offsetY, 0, c));
            }
        }
        return tick -> points;
    }

    public static Generator text(String text, String fontName, @MagicConstant(flags = {Font.PLAIN,Font.BOLD,Font.ITALIC}) int style, ColorProvider color, int fontSize, double size) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Font f = new Font(fontName, style, fontSize);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text);
        int h = fm.getHeight();
        g.dispose();

        img = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
        g = img.createGraphics();
        g.setFont(f);
        g.setColor(java.awt.Color.WHITE);
        g.drawString(text, 0, fm.getAscent());
        g.dispose();

        Generator base = fromImage(img, size, 1);
        return tick -> {
            List<Vector> points = base.generate(tick);
            List<Vector> colored = new ArrayList<>(points.size());
            double maxX = points.stream().mapToDouble(Vector::getX).max().orElse(1);
            double minX = points.stream().mapToDouble(Vector::getX).min().orElse(0);
            double range = maxX - minX;

            for (Vector v : points) {
                double progress = (v.getX() - minX) / range;
                colored.add(new Pixel(v.getX(), v.getY(), v.getZ(), color.get(v, progress)));
            }
            return colored;
        };
    }

    public static Generator text(String text, String fontName, @MagicConstant(flags = {Font.PLAIN,Font.BOLD,Font.ITALIC}) int style, int fontSize, double size) {
        return text(text, fontName, style, ColorProvider.fixed(Color.WHITE), fontSize, size);
    }

    public static Generator point() {
        List<Vector> p = List.of(new Vector());
        return tick -> p;
    }

    public static Generator line(Vector start, Vector end, double step) {
        List<Vector> points = new ArrayList<>();
        Vector dir = end.clone().subtract(start);
        double len = dir.length();
        dir.normalize().multiply(step);
        for (double d = 0; d < len; d += step) {
            points.add(start.clone().add(dir.clone().multiply(d)));
        }
        return tick -> points;
    }

    public static Generator circle(double radius, int points) {
        List<Vector> base = new ArrayList<>(points);
        double inc = Math.PI * 2 / points;
        for (int i = 0; i < points; i++) {
            base.add(new Vector(Math.cos(i * inc) * radius, 0, Math.sin(i * inc) * radius));
        }
        return tick -> base;
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
        List<Vector> base = new ArrayList<>(points);
        double phi = Math.PI * (3. - Math.sqrt(5.));
        for (int i = 0; i < points; i++) {
            double y = 1 - (i / (double) (points - 1)) * 2;
            double r = Math.sqrt(1 - y * y);
            double theta = phi * i;
            base.add(new Vector(Math.cos(theta) * r, y, Math.sin(theta) * r).multiply(radius));
        }
        return tick -> base;
    }

    public static Generator cube(double size, int resolution) {
        List<Vector> list = new ArrayList<>();
        double half = size / 2.0;
        double step = size / resolution;

        for (int x = 0; x <= resolution; x++) {
            for (int y = 0; y <= resolution; y++) {
                for (int z = 0; z <= resolution; z++) {
                    boolean onSurface = x == 0 || x == resolution || y == 0 || y == resolution || z == 0 || z == resolution;
                    if (onSurface) {
                        list.add(new Vector(-half + x * step, -half + y * step, -half + z * step));
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
                    list.add(new Vector(-half + x * step, t * height, -half + z * step));
                }
            }
        }
        return tick -> list;
    }

    public static Generator rotatingCircle(double radius, int points, double speed) {
        List<Vector> base = new ArrayList<>(points);
        double inc = Math.PI * 2 / points;
        for (int i = 0; i < points; i++) {
            base.add(new Vector(Math.cos(i * inc) * radius, 0, Math.sin(i * inc) * radius));
        }
        return tick -> {
            List<Vector> out = new ArrayList<>(points);
            double rot = tick * speed;
            for (Vector v : base) out.add(v.clone().rotateAroundY(rot));
            return out;
        };
    }

    public static Generator orbit(double radius, int points, double speed) {
        List<Vector> base = new ArrayList<>(points);
        double step = Math.PI * 2 / points;
        for (int i = 0; i < points; i++) {
            double a = i * step;
            base.add(new Vector(Math.cos(a) * radius, 0, Math.sin(a) * radius));
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
            base.add(new Vector(Math.cos(a) * radius, 0, Math.sin(a) * radius));
        }
        return tick -> {
            int visible = duration <= 0 ? totalPoints : (int) Math.min(totalPoints, totalPoints * tick / duration);
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
                    boolean onSurface = x == 0 || x == resolution || y == 0 || y == resolution || z == 0 || z == resolution;
                    if (onSurface) {
                        base.add(new Vector(-half + x * step, -half + y * step, -half + z * step));
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
            base.add(new Vector(Math.cos(a), 0, Math.sin(a)));
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

    public static Generator helix(double radius, double height, int turns, int points, double speed) {
        List<Vector> base = new ArrayList<>(points);
        double yStep = height / points;
        double angStep = (Math.PI * 2 * turns) / points;
        for (int i = 0; i < points; i++) {
            base.add(new Vector(Math.cos(i * angStep) * radius, i * yStep, Math.sin(i * angStep) * radius));
        }
        return tick -> {
            List<Vector> out = new ArrayList<>(points);
            double rot = tick * speed;
            for (Vector v : base) out.add(v.clone().rotateAroundY(rot));
            return out;
        };
    }

    public static Generator spiral(double maxRadius, double height, int points, double speed) {
        List<Vector> base = new ArrayList<>(points);
        for (int i = 0; i < points; i++) {
            double t = i / (double) points;
            double r = maxRadius * t;
            double a = t * Math.PI * 2;
            base.add(new Vector(Math.cos(a) * r, t * height, Math.sin(a) * r));
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