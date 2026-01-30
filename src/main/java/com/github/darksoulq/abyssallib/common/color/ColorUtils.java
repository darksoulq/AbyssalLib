package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A comprehensive utility class for color manipulation, conversion, and generation.
 * <p>
 * This class provides methods to interface between Bukkit {@link Color} objects and
 * various color spaces including RGB, HEX, HSB, CMYK, XYZ, Lab, Lch, and OkLab.
 * It also includes advanced blending, mixing, and color theory palette generation.
 */
public final class ColorUtils {
    /** Internal {@link Random} instance used for generating unpredictable colors. */
    private static final Random RANDOM = new Random();

    /** * A thread-safe mapping of lowercase color names to their respective Bukkit
     * {@link Color} constants.
     */
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

    /**
     * Retrieves a color by its common English name.
     *
     * @param name The name of the color (case-insensitive).
     * @return The matching {@link Color}, or {@code null} if the name is not recognized.
     */
    public static Color fromName(String name) {
        return NAMED_COLORS.get(name.toLowerCase(Locale.ROOT));
    }

    /**
     * Parses a hexadecimal string into a Bukkit Color.
     * <p>
     * Supports standard 6-digit hex (e.g., "#FF0000") and shorthand 3-digit hex (e.g., "#F00").
     *
     * @param hex The hexadecimal string, with or without a leading '#' character.
     * @return The resulting {@link Color} object.
     * @throws NumberFormatException if the string is not a valid hexadecimal value.
     */
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

    /**
     * Converts a Color object into a standard CSS-style hexadecimal string.
     *
     * @param color The {@link Color} to convert.
     * @return A string formatted as "#RRGGBB" in lowercase.
     */
    public static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Converts a Color into a single packed RGB integer.
     *
     * @param color The {@link Color} to convert.
     * @return An integer representing the RGB value (0xRRGGBB).
     */
    public static int toInt(Color color) {
        return color.asRGB();
    }

    /**
     * Creates a Color object from a packed RGB integer.
     *
     * @param rgb The integer value (0xRRGGBB). Bitmask 0xFFFFFF is applied.
     * @return The resulting {@link Color}.
     */
    public static Color fromInt(int rgb) {
        return Color.fromRGB(rgb & 0xFFFFFF);
    }

    /**
     * Creates a Color using the HSB (Hue, Saturation, Brightness) color model.
     *
     *
     *
     * @param hue        The hue component (0.0 to 1.0).
     * @param saturation The saturation component (0.0 to 1.0).
     * @param brightness The brightness/value component (0.0 to 1.0).
     * @return The resulting {@link Color}.
     */
    public static Color hsb(float hue, float saturation, float brightness) {
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
        return Color.fromRGB(rgb & 0xFFFFFF);
    }

    /**
     * Converts a Color into an array of HSB components.
     *
     * @param color The {@link Color} to convert.
     * @return A float array where [0] is Hue, [1] is Saturation, and [2] is Brightness.
     */
    public static float[] toHSB(Color color) {
        return java.awt.Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }

    /**
     * Creates a Color using the CMYK color model.
     *
     * @param c Cyan component (0.0 to 1.0).
     * @param m Magenta component (0.0 to 1.0).
     * @param y Yellow component (0.0 to 1.0).
     * @param k Black/Key component (0.0 to 1.0).
     * @return The resulting {@link Color}.
     */
    public static Color cmyk(float c, float m, float y, float k) {
        return ColorConverter.fromCMYK(c, m, y, k);
    }

    /**
     * Converts a Color to CMYK components.
     *
     * @param color The {@link Color} to convert.
     * @return A float array containing C, M, Y, K in order.
     */
    public static float[] toCMYK(Color color) {
        return ColorConverter.toCMYK(color);
    }

    /**
     * Creates a Color from CIE XYZ coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The resulting {@link Color}.
     */
    public static Color xyz(double x, double y, double z) {
        return ColorConverter.fromXYZ(x, y, z);
    }

    /**
     * Converts a Color to CIE XYZ coordinates.
     *
     * @param c The {@link Color} to convert.
     * @return A double array containing X, Y, Z.
     */
    public static double[] toXYZ(Color c) {
        return ColorConverter.toXYZ(c);
    }

    /**
     * Creates a Color from CIE Lab coordinates.
     *
     * @param l Lightness.
     * @param a The A axis (green to red).
     * @param b The B axis (blue to yellow).
     * @return The resulting {@link Color}.
     */
    public static Color lab(double l, double a, double b) {
        return ColorConverter.fromLab(l, a, b);
    }

    /**
     * Converts a Color to CIE Lab coordinates.
     *
     * @param c The {@link Color} to convert.
     * @return A double array containing L, a, b.
     */
    public static double[] toLab(Color c) {
        return ColorConverter.toLab(c);
    }

    /**
     * Creates a Color from Lch (Lightness, Chroma, Hue) coordinates.
     *
     * @param l Lightness.
     * @param c Chroma.
     * @param h Hue (in degrees).
     * @return The resulting {@link Color}.
     */
    public static Color lch(double l, double c, double h) {
        return ColorConverter.fromLch(l, c, h);
    }

    /**
     * Converts a Color to Lch coordinates.
     *
     * @param color The {@link Color} to convert.
     * @return A double array containing L, c, h.
     */
    public static double[] toLch(Color color) {
        return ColorConverter.toLch(color);
    }

    /**
     * Performs a linear RGB interpolation (Lerp) between two colors.
     *
     * @param c1    The starting color.
     * @param c2    The target color.
     * @param ratio The interpolation factor (0.0 to 1.0).
     * @return The mixed {@link Color}.
     */
    public static Color mix(Color c1, Color c2, double ratio) {
        if (ratio <= 0) return c1;
        if (ratio >= 1) return c2;
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        return Color.fromRGB(r, g, b);
    }

    /**
     * Interpolates two colors within the OkLab color space.
     * <p>
     * This provides perceptually linear transitions that avoid the desaturation
     * common in standard RGB mixing.
     *
     *
     *
     * @param c1    The starting color.
     * @param c2    The target color.
     * @param ratio The interpolation factor (0.0 to 1.0).
     * @return The mixed {@link Color}.
     */
    public static Color mixOkLab(Color c1, Color c2, double ratio) {
        double[] lab1 = ColorConverter.toOkLab(c1);
        double[] lab2 = ColorConverter.toOkLab(c2);

        double l = lab1[0] + (lab2[0] - lab1[0]) * ratio;
        double a = lab1[1] + (lab2[1] - lab1[1]) * ratio;
        double b = lab1[2] + (lab2[2] - lab1[2]) * ratio;

        return ColorConverter.fromOkLab(l, a, b);
    }

    /**
     * Interpolates two colors within the Lch color space.
     * <p>
     * This is ideal for gradients that need to maintain consistent lightness and
     * chroma while shifting hue, particularly across the shortest path of the hue circle.
     *
     * @param c1    The starting color.
     * @param c2    The target color.
     * @param ratio The interpolation factor (0.0 to 1.0).
     * @return The mixed {@link Color}.
     */
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

    /**
     * Blends two colors using a specific {@link BlendMode}.
     *
     * @param c1   The base color.
     * @param c2   The overlay color.
     * @param mode The logic used to combine the colors.
     * @return The resulting {@link Color}.
     */
    public static Color blend(Color c1, Color c2, BlendMode mode) {
        return mode.blend(c1, c2);
    }

    /**
     * Calculates the mathematical average of a collection of colors.
     *
     * @param colors The collection of {@link Color} objects.
     * @return The average {@link Color}, or {@link Color#WHITE} if the collection is empty.
     */
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

    /**
     * Computes the squared Euclidean distance between two colors in RGB space.
     *
     * @param c1 The first color.
     * @param c2 The second color.
     * @return The squared distance value.
     */
    public static double distanceSq(Color c1, Color c2) {
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return r * r + g * g + b * b;
    }

    /**
     * Computes the perceptual Delta-E distance between two colors in Lab space.
     *
     * @param c1 The first color.
     * @param c2 The second color.
     * @return The Delta-E distance.
     */
    public static double distanceLab(Color c1, Color c2) {
        double[] lab1 = toLab(c1);
        double[] lab2 = toLab(c2);
        return Math.sqrt(Math.pow(lab1[0] - lab2[0], 2) + Math.pow(lab1[1] - lab2[1], 2) + Math.pow(lab1[2] - lab2[2], 2));
    }

    /**
     * Finds the color in a palette that is closest to the target color.
     *
     * @param target  The color to match.
     * @param palette The pool of available colors.
     * @return The closest {@link Color} found, or {@link Color#WHITE} if palette is empty.
     */
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

    /**
     * Generates a random RGB color.
     *
     * @return A randomly generated {@link Color}.
     */
    public static Color random() {
        return Color.fromRGB(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));
    }

    /**
     * Adjusts the saturation of a color.
     *
     * @param color  The input color.
     * @param factor The multiplier (e.g., 2.0 for double saturation, 0.5 for half).
     * @return The saturated {@link Color}.
     */
    public static Color saturate(Color color, float factor) {
        float[] hsb = toHSB(color);
        hsb[1] = Math.min(1.0f, Math.max(0.0f, hsb[1] * factor));
        return hsb(hsb[0], hsb[1], hsb[2]);
    }

    /**
     * Reduces the saturation of a color.
     *
     * @param color  The input color.
     * @param factor The reduction amount (0.0 to 1.0).
     * @return The desaturated {@link Color}.
     */
    public static Color desaturate(Color color, float factor) {
        return saturate(color, 1f - factor);
    }

    /**
     * Adjusts the brightness of a color.
     *
     * @param color  The input color.
     * @param factor The multiplier for the brightness component.
     * @return The brightened {@link Color}.
     */
    public static Color brighten(Color color, float factor) {
        float[] hsb = toHSB(color);
        hsb[2] = Math.min(1.0f, Math.max(0.0f, hsb[2] * factor));
        return hsb(hsb[0], hsb[1], hsb[2]);
    }

    /**
     * Reduces the brightness of a color.
     *
     * @param color  The input color.
     * @param factor The reduction amount (0.0 to 1.0).
     * @return The darkened {@link Color}.
     */
    public static Color darken(Color color, float factor) {
        return brighten(color, 1f - factor);
    }

    /**
     * Shifts the hue of a color by a specific degree on the hue circle.
     *
     * @param color   The input color.
     * @param degrees The amount to shift (e.g., 360 is a full rotation).
     * @return The hue-shifted {@link Color}.
     */
    public static Color hueShift(Color color, float degrees) {
        float[] hsb = toHSB(color);
        hsb[0] = (hsb[0] + (degrees / 360.0f)) % 1.0f;
        if (hsb[0] < 0) hsb[0] += 1.0f;
        return hsb(hsb[0], hsb[1], hsb[2]);
    }

    /**
     * Inverts the RGB components of a color.
     *
     * @param color The {@link Color} to invert.
     * @return The inverted {@link Color}.
     */
    public static Color invert(Color color) {
        return Color.fromRGB(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }

    /**
     * Converts a color to its grayscale equivalent using a simple average.
     *
     * @param color The {@link Color} to convert.
     * @return The grayscale {@link Color}.
     */
    public static Color grayscale(Color color) {
        int avg = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
        return Color.fromRGB(avg, avg, avg);
    }

    /**
     * Determines whether black or white provides better contrast against a background.
     * <p>
     * Uses standard relative luminance coefficients: 0.299R, 0.587G, 0.114B.
     *
     * @param bg The background {@link Color}.
     * @return {@link Color#BLACK} for light backgrounds, {@link Color#WHITE} for dark.
     */
    public static Color contrast(Color bg) {
        double lum = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255;
        return lum > 0.5 ? Color.BLACK : Color.WHITE;
    }

    /**
     * Finds the complementary color (180-degree hue shift).
     *
     * @param color The input color.
     * @return The complementary {@link Color}.
     */
    public static Color complementary(Color color) {
        return hueShift(color, 180);
    }

    /**
     * Generates a split-complementary color scheme.
     *
     * @param color The base color.
     * @return A list containing the base, and two colors 150 and 210 degrees away.
     */
    public static List<Color> splitComplementary(Color color) {
        return List.of(color, hueShift(color, 150), hueShift(color, 210));
    }

    /**
     * Generates a triadic color scheme.
     *
     * @param color The base color.
     * @return A list containing three colors spaced 120 degrees apart.
     */
    public static List<Color> triadic(Color color) {
        return List.of(color, hueShift(color, 120), hueShift(color, 240));
    }

    /**
     * Generates a tetradic (double-complementary) color scheme.
     *
     * @param color The base color.
     * @return A list containing four colors spaced 90 degrees apart.
     */
    public static List<Color> tetradic(Color color) {
        return List.of(color, hueShift(color, 90), hueShift(color, 180), hueShift(color, 270));
    }

    /**
     * Generates an analogous color scheme.
     *
     * @param color The base color.
     * @return A list containing the base and its two immediate neighbors (±30 degrees).
     */
    public static List<Color> analogous(Color color) {
        return List.of(hueShift(color, -30), color, hueShift(color, 30));
    }

    /**
     * Generates a monochromatic color scheme by varying brightness and saturation.
     *
     * @param color The base color.
     * @return A list of five colors derived from the base.
     */
    public static List<Color> monochromatic(Color color) {
        return List.of(
            color,
            brighten(color, 0.5f),
            brighten(color, 1.5f),
            saturate(color, 0.5f),
            saturate(color, 1.5f)
        );
    }

    /**
     * Generates a list of colors representing a gradient between two colors.
     *
     * @param start The starting color.
     * @param end   The ending color.
     * @param steps The number of colors to generate.
     * @return A list of colors in the gradient.
     */
    public static List<Color> gradient(Color start, Color end, int steps) {
        List<Color> list = new ArrayList<>(steps);
        for (int i = 0; i < steps; i++) {
            list.add(mix(start, end, i / (double) (Math.max(1, steps - 1))));
        }
        return list;
    }

    /**
     * Extracts a color palette from an image using a sparse sampling method.
     *
     * @param image     The {@link BufferedImage} to sample.
     * @param maxColors The maximum number of unique colors to return.
     * @return A list of colors extracted from the image.
     */
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