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

/**
 * A utility class containing static factory methods for creating {@link Generator} instances.
 * <p>
 * This class includes generators for 2D/3D shapes, text-to-particle conversion,
 * image-to-particle conversion, and dynamic path animations.
 */
public final class Generators {

    /**
     * Creates a generator that replicates a {@link BufferedImage} using particles.
     *
     * @param image   The source image to convert.
     * @param size    The maximum world-space size the image should occupy.
     * @param density The pixel skip rate (1 = every pixel, 2 = every other pixel).
     * @return A {@link Generator} representing the image.
     */
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

                Color c = Color.fromRGB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
                points.add(new Pixel(x * scale - offsetX, (height - y) * scale - offsetY, 0, c));
            }
        }
        return tick -> points;
    }

    /**
     * Converts a string of text into a particle-based layout with custom coloring.
     *
     * @param text     The text to display.
     * @param fontName The name of the font to use.
     * @param style    The font style (e.g., {@link Font#BOLD}).
     * @param color    The {@link ColorProvider} for the text color.
     * @param fontSize The size of the font in the internal buffer.
     * @param size     The world-space width/height of the rendered text.
     * @return A {@link Generator} representing the text.
     */
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

    /**
     * Converts a string of text into white particles.
     *
     * @param text     The text.
     * @param fontName The font.
     * @param style    The font style.
     * @param fontSize The font size.
     * @param size     The world size.
     * @return A {@link Generator} for plain text.
     */
    public static Generator text(String text, String fontName, @MagicConstant(flags = {Font.PLAIN,Font.BOLD,Font.ITALIC}) int style, int fontSize, double size) {
        return text(text, fontName, style, ColorProvider.fixed(Color.WHITE), fontSize, size);
    }

    /**
     * @return A generator producing a single point at (0,0,0).
     */
    public static Generator point() {
        List<Vector> p = List.of(new Vector());
        return tick -> p;
    }

    /**
     * Generates a line of particles between two points.
     *
     * @param start The starting {@link Vector}.
     * @param end   The ending {@link Vector}.
     * @param step  The distance between each particle.
     * @return A {@link Generator} for a line.
     */
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

    /**
     * Generates a flat circle on the XZ plane.
     *
     * @param radius The radius of the circle.
     * @param points The number of particles in the circumference.
     * @return A {@link Generator} for a circle.
     */
    public static Generator circle(double radius, int points) {
        List<Vector> base = new ArrayList<>(points);
        double inc = Math.PI * 2 / points;
        for (int i = 0; i < points; i++) {
            base.add(new Vector(Math.cos(i * inc) * radius, 0, Math.sin(i * inc) * radius));
        }
        return tick -> base;
    }

    /**
     * Generates a flat square outline on the XZ plane.
     *
     * @param size          The side length of the square.
     * @param pointsPerSide The number of particles per edge.
     * @return A {@link Generator} for a square.
     */
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

    /**
     * Generates a 3D sphere using a Fibonacci spiral distribution.
     *
     * @param radius The radius of the sphere.
     * @param points The total number of particles.
     * @return A {@link Generator} for a sphere.
     */
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

    /**
     * Generates the wireframe of a cube.
     *
     * @param size       The edge length.
     * @param resolution The number of particles per edge.
     * @return A {@link Generator} for a cube.
     */
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

    /**
     * Generates a pyramid shape.
     *
     * @param size       The base width.
     * @param height     The height.
     * @param resolution The particle density per layer.
     * @return A {@link Generator} for a pyramid.
     */
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

    /**
     * Generates a circle that rotates around the Y axis over time.
     *
     * @param radius The radius.
     * @param points The particle count.
     * @param speed  The rotation speed.
     * @return A dynamic {@link Generator}.
     */
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

    /**
     * Generates a circular orbit animation.
     *
     * @param radius The orbit radius.
     * @param points The particle count.
     * @param speed  The orbital speed.
     * @return A dynamic {@link Generator}.
     */
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

    /**
     * Generates a circle that "draws" itself over a duration.
     *
     * @param radius      The radius.
     * @param totalPoints The final particle count.
     * @param duration    Ticks to complete the drawing.
     * @return A dynamic {@link Generator}.
     */
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

    /**
     * Generates a cube that "grows" from bottom to top over a duration.
     *
     * @param size       The cube size.
     * @param resolution The particle density.
     * @param duration   Ticks to reach full height.
     * @return A dynamic {@link Generator}.
     */
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

    /**
     * Generates a moving sine wave.
     *
     * @param length    The total wave length.
     * @param points    The number of particles.
     * @param amplitude The height of the peaks.
     * @param frequency The wave frequency.
     * @param speed     The wave speed (phase shift).
     * @return A dynamic {@link Generator}.
     */
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

    /**
     * Generates a circle that pulses (changes radius) over time.
     *
     * @param baseRadius     The median radius.
     * @param pulseAmplitude The maximum change from median.
     * @param speed          The pulse speed.
     * @param points         The number of particles.
     * @return A dynamic {@link Generator}.
     */
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

    /**
     * Generates a 3D helix (spiral) shape.
     *
     * @param radius The radius of the helix.
     * @param height The total height.
     * @param turns  The number of complete rotations.
     * @param points The total number of particles.
     * @param speed  The rotation speed.
     * @return A dynamic {@link Generator}.
     */
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

    /**
     * Generates a cone-like spiral starting from a point.
     *
     * @param maxRadius The final radius at the top.
     * @param height    The total height.
     * @param points    The total number of particles.
     * @param speed     The rotation speed.
     * @return A dynamic {@link Generator}.
     */
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