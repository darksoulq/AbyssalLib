package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

public final class ColorUtils {
    private static final Random RANDOM = new Random();
    private static final Map<String, Color> NAMED_COLORS = new HashMap<>();

    static {
        NAMED_COLORS.put("white", Color.WHITE);
        NAMED_COLORS.put("silver", Color.SILVER);
        NAMED_COLORS.put("gray", Color.GRAY);
        NAMED_COLORS.put("black", Color.BLACK);
        NAMED_COLORS.put("red", Color.RED);
        NAMED_COLORS.put("maroon", Color.MAROON);
        NAMED_COLORS.put("yellow", Color.YELLOW);
        NAMED_COLORS.put("olive", Color.OLIVE);
        NAMED_COLORS.put("lime", Color.LIME);
        NAMED_COLORS.put("green", Color.GREEN);
        NAMED_COLORS.put("aqua", Color.AQUA);
        NAMED_COLORS.put("teal", Color.TEAL);
        NAMED_COLORS.put("blue", Color.BLUE);
        NAMED_COLORS.put("navy", Color.NAVY);
        NAMED_COLORS.put("fuchsia", Color.FUCHSIA);
        NAMED_COLORS.put("purple", Color.PURPLE);
        NAMED_COLORS.put("orange", Color.ORANGE);
    }

    public static Color fromName(String name) {
        return NAMED_COLORS.get(name.toLowerCase(Locale.ROOT));
    }

    public static Color hex(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (hex.length() == 3) {
            char r = hex.charAt(0);
            char g = hex.charAt(1);
            char b = hex.charAt(2);
            hex = "" + r + r + g + g + b + b;
        }
        return Color.fromRGB(Integer.parseInt(hex, 16));
    }

    public static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static int toInt(Color color) {
        return color.asRGB();
    }

    public static Color fromInt(int rgb) {
        return Color.fromRGB(rgb & 0xFFFFFF);
    }

    public static Color hsb(float hue, float saturation, float brightness) {
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
        return Color.fromRGB(rgb & 0xFFFFFF);
    }

    public static float[] toHSB(Color color) {
        return java.awt.Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }

    public static Color cmyk(float c, float m, float y, float k) {
        return ColorConverter.fromCMYK(c, m, y, k);
    }

    public static float[] toCMYK(Color color) {
        return ColorConverter.toCMYK(color);
    }

    public static Color xyz(double x, double y, double z) {
        return ColorConverter.fromXYZ(x, y, z);
    }

    public static double[] toXYZ(Color c) {
        return ColorConverter.toXYZ(c);
    }

    public static Color lab(double l, double a, double b) {
        return ColorConverter.fromLab(l, a, b);
    }

    public static double[] toLab(Color c) {
        return ColorConverter.toLab(c);
    }

    public static Color lch(double l, double c, double h) {
        return ColorConverter.fromLch(l, c, h);
    }

    public static double[] toLch(Color color) {
        return ColorConverter.toLch(color);
    }

    public static Color mix(Color c1, Color c2, double ratio) {
        if (ratio <= 0) return c1;
        if (ratio >= 1) return c2;
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        return Color.fromRGB(r, g, b);
    }

    public static Color mixOkLab(Color c1, Color c2, double ratio) {
        double[] lab1 = ColorConverter.toOkLab(c1);
        double[] lab2 = ColorConverter.toOkLab(c2);
        
        double l = lab1[0] + (lab2[0] - lab1[0]) * ratio;
        double a = lab1[1] + (lab2[1] - lab1[1]) * ratio;
        double b = lab1[2] + (lab2[2] - lab1[2]) * ratio;
        
        return ColorConverter.fromOkLab(l, a, b);
    }

    public static Color mixLch(Color c1, Color c2, double ratio) {
        double[] lch1 = ColorConverter.toLch(c1);
        double[] lch2 = ColorConverter.toLch(c2);

        double l = lch1[0] + (lch2[0] - lch1[0]) * ratio;
        double c = lch1[1] + (lch2[1] - lch1[1]) * ratio;
        
        double h;
        double d = lch2[2] - lch1[2];
        if (lch1[1] == 0 || lch2[1] == 0) {
            h = lch1[1] == 0 ? lch2[2] : lch1[2];
        } else {
            if (d > 180) d -= 360;
            if (d < -180) d += 360;
            h = lch1[2] + d * ratio;
        }
        
        return ColorConverter.fromLch(l, c, h);
    }

    public static Color blend(Color c1, Color c2, BlendMode mode) {
        return mode.blend(c1, c2);
    }

    public static Color average(Collection<Color> colors) {
        if (colors.isEmpty()) return Color.WHITE;
        long r = 0, g = 0, b = 0;
        for (Color c : colors) {
            r += c.getRed();
            g += c.getGreen();
            b += c.getBlue();
        }
        return Color.fromRGB((int) (r / colors.size()), (int) (g / colors.size()), (int) (b / colors.size()));
    }

    public static double distanceSq(Color c1, Color c2) {
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return r * r + g * g + b * b;
    }

    public static double distanceLab(Color c1, Color c2) {
        double[] lab1 = toLab(c1);
        double[] lab2 = toLab(c2);
        return Math.sqrt(Math.pow(lab1[0] - lab2[0], 2) + Math.pow(lab1[1] - lab2[1], 2) + Math.pow(lab1[2] - lab2[2], 2));
    }

    public static Color closest(Color target, Collection<Color> palette) {
        Color best = null;
        double minDist = Double.MAX_VALUE;
        for (Color c : palette) {
            double d = distanceSq(target, c);
            if (d < minDist) {
                minDist = d;
                best = c;
            }
        }
        return best != null ? best : Color.WHITE;
    }

    public static Color random() {
        return Color.fromRGB(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));
    }

    public static Color saturate(Color color, float factor) {
        float[] hsb = toHSB(color);
        hsb[1] = Math.min(1.0f, Math.max(0.0f, hsb[1] * factor));
        return hsb(hsb[0], hsb[1], hsb[2]);
    }

    public static Color desaturate(Color color, float factor) {
        return saturate(color, 1f - factor);
    }

    public static Color brighten(Color color, float factor) {
        float[] hsb = toHSB(color);
        hsb[2] = Math.min(1.0f, Math.max(0.0f, hsb[2] * factor));
        return hsb(hsb[0], hsb[1], hsb[2]);
    }

    public static Color darken(Color color, float factor) {
        return brighten(color, 1f - factor);
    }

    public static Color hueShift(Color color, float degrees) {
        float[] hsb = toHSB(color);
        hsb[0] = (hsb[0] + (degrees / 360.0f)) % 1.0f;
        if (hsb[0] < 0) hsb[0] += 1.0f;
        return hsb(hsb[0], hsb[1], hsb[2]);
    }

    public static Color invert(Color color) {
        return Color.fromRGB(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }

    public static Color grayscale(Color color) {
        int avg = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
        return Color.fromRGB(avg, avg, avg);
    }

    public static Color contrast(Color bg) {
        double lum = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255;
        return lum > 0.5 ? Color.BLACK : Color.WHITE;
    }

    public static Color complementary(Color color) {
        return hueShift(color, 180);
    }

    public static List<Color> splitComplementary(Color color) {
        return List.of(color, hueShift(color, 150), hueShift(color, 210));
    }

    public static List<Color> triadic(Color color) {
        return List.of(color, hueShift(color, 120), hueShift(color, 240));
    }

    public static List<Color> tetradic(Color color) {
        return List.of(color, hueShift(color, 90), hueShift(color, 180), hueShift(color, 270));
    }

    public static List<Color> analogous(Color color) {
        return List.of(hueShift(color, -30), color, hueShift(color, 30));
    }

    public static List<Color> monochromatic(Color color) {
        return List.of(
            color,
            brighten(color, 0.5f),
            brighten(color, 1.5f),
            saturate(color, 0.5f),
            saturate(color, 1.5f)
        );
    }

    public static List<Color> gradient(Color start, Color end, int steps) {
        List<Color> list = new ArrayList<>(steps);
        for (int i = 0; i < steps; i++) {
            list.add(mix(start, end, i / (double) (Math.max(1, steps - 1))));
        }
        return list;
    }

    public static List<Color> palette(BufferedImage image, int maxColors) {
        Set<Color> colors = new HashSet<>();
        int step = Math.max(1, (image.getWidth() * image.getHeight()) / 500);
        for (int x = 0; x < image.getWidth(); x += step) {
            for (int y = 0; y < image.getHeight(); y += step) {
                int rgb = image.getRGB(x, y);
                if (((rgb >> 24) & 0xFF) > 10) colors.add(Color.fromRGB(rgb & 0xFFFFFF));
            }
        }
        return colors.stream().limit(maxColors).collect(Collectors.toList());
    }
}