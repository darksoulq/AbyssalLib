package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

/**
 * A specialized utility class for converting between various color spaces.
 * <p>
 * This class provides low-level mathematical conversions for:
 * <ul>
 * <li><b>RGB / Hex:</b> Standard web and computer colors.</li>
 * <li><b>HSB:</b> Hue, Saturation, and Brightness.</li>
 * <li><b>CMYK:</b> Cyan, Magenta, Yellow, and Key (Black) for print modeling.</li>
 * <li><b>CIE XYZ:</b> The device-independent color space foundation.</li>
 * <li><b>CIE Lab:</b> A perceptually uniform color space.</li>
 * <li><b>OkLab:</b> A modern perceptually uniform space designed for better gradients.</li>
 * <li><b>LCH:</b> Cylindrical representation of Lab (Lightness, Chroma, Hue).</li>
 * </ul>
 */
public final class ColorConverter {

    /**
     * Parses a hexadecimal string into a Bukkit {@link Color}.
     *
     * @param hex The hexadecimal string (e.g., "#FFFFFF").
     * @return The resulting Color object.
     */
    public static Color fromHex(String hex) {
        return ColorUtils.hex(hex);
    }

    /**
     * Converts a Color into a hexadecimal string representation.
     *
     * @param color The {@link Color} to convert.
     * @return A string formatted as "#RRGGBB".
     */
    public static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Extracts HSB components from a Color.
     *
     * @param color The {@link Color} to convert.
     * @return A float array containing [Hue, Saturation, Brightness].
     */
    public static float[] toHSB(Color color) {
        return java.awt.Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }

    /**
     * Creates a Color from HSB components.
     *
     * @param h Hue (0.0 to 1.0).
     * @param s Saturation (0.0 to 1.0).
     * @param b Brightness (0.0 to 1.0).
     * @return The resulting {@link Color}.
     */
    public static Color fromHSB(float h, float s, float b) {
        return ColorUtils.hsb(h, s, b);
    }

    /**
     * Converts a Color into CMYK components.
     *
     * @param color The {@link Color} to convert.
     * @return A float array containing [C, M, Y, K] values (0.0 to 1.0).
     */
    public static float[] toCMYK(Color color) {
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float k = 1 - Math.max(r, Math.max(g, b));
        if (k == 1) return new float[]{0, 0, 0, 1};
        float c = (1 - r - k) / (1 - k);
        float m = (1 - g - k) / (1 - k);
        float y = (1 - b - k) / (1 - k);
        return new float[]{c, m, y, k};
    }

    /**
     * Creates a Color from CMYK components.
     *
     * @param c Cyan (0.0 to 1.0).
     * @param m Magenta (0.0 to 1.0).
     * @param y Yellow (0.0 to 1.0).
     * @param k Key/Black (0.0 to 1.0).
     * @return The resulting {@link Color}.
     */
    public static Color fromCMYK(float c, float m, float y, float k) {
        int r = (int) (255 * (1 - c) * (1 - k));
        int g = (int) (255 * (1 - m) * (1 - k));
        int b = (int) (255 * (1 - y) * (1 - k));
        return Color.fromRGB(clamp(r), clamp(g), clamp(b));
    }

    /**
     * Converts a Color to CIE XYZ space.
     * Uses sRGB D65 constants and performs linearization (gamma expansion).
     *
     * @param c The {@link Color} to convert.
     * @return A double array containing [X, Y, Z].
     */
    public static double[] toXYZ(Color c) {
        double r = pivotRGB(c.getRed() / 255.0);
        double g = pivotRGB(c.getGreen() / 255.0);
        double b = pivotRGB(c.getBlue() / 255.0);
        return new double[]{
            r * 0.4124 + g * 0.3576 + b * 0.1805,
            r * 0.2126 + g * 0.7152 + b * 0.0722,
            r * 0.0193 + g * 0.1192 + b * 0.9505
        };
    }

    /**
     * Converts CIE XYZ coordinates back into a Color.
     *
     * @param x Coordinate X.
     * @param y Coordinate Y.
     * @param z Coordinate Z.
     * @return The resulting {@link Color}.
     */
    public static Color fromXYZ(double x, double y, double z) {
        double r = x * 3.2406 + y * -1.5372 + z * -0.4986;
        double g = x * -0.9689 + y * 1.8758 + z * 0.0415;
        double b = x * 0.0557 + y * -0.2040 + z * 1.0570;
        return Color.fromRGB(unpivotRGB(r), unpivotRGB(g), unpivotRGB(b));
    }

    /**
     * Converts a Color to CIE Lab space.
     * This space is designed to be perceptually uniform, where a change in value
     * corresponds to a similar change in human perception.
     *
     * @param c The {@link Color} to convert.
     * @return A double array containing [L, a, b].
     */
    public static double[] toLab(Color c) {
        double[] xyz = toXYZ(c);
        double x = pivotXYZ(xyz[0] / 0.95047);
        double y = pivotXYZ(xyz[1]);
        double z = pivotXYZ(xyz[2] / 1.08883);
        return new double[]{116 * y - 16, 500 * (x - y), 200 * (y - z)};
    }

    /**
     * Creates a Color from CIE Lab coordinates.
     *
     * @param l Lightness (0.0 to 100.0).
     * @param a The green-red axis.
     * @param b The blue-yellow axis.
     * @return The resulting {@link Color}.
     */
    public static Color fromLab(double l, double a, double b) {
        double y = (l + 16) / 116.0;
        double x = a / 500.0 + y;
        double z = y - b / 200.0;
        x = unpivotXYZ(x) * 0.95047;
        y = unpivotXYZ(y);
        z = unpivotXYZ(z) * 1.08883;
        return fromXYZ(x, y, z);
    }

    /**
     * Converts a Color to the OkLab space.
     * OkLab is a modern perceptually uniform space that improves upon CIELAB,
     * specifically for better hue preservation and uniform lightness.
     *
     * @param c The {@link Color} to convert.
     * @return A double array containing [L, a, b] in OkLab format.
     */
    public static double[] toOkLab(Color c) {
        double r = c.getRed() / 255.0;
        double g = c.getGreen() / 255.0;
        double b = c.getBlue() / 255.0;

        double l = 0.4122214708f * r + 0.5363325363f * g + 0.0514459929f * b;
        double m = 0.2119034982f * r + 0.6806995451f * g + 0.1073969566f * b;
        double s = 0.0883024619f * r + 0.2817188376f * g + 0.6299787005f * b;

        double l_ = Math.cbrt(l);
        double m_ = Math.cbrt(m);
        double s_ = Math.cbrt(s);

        return new double[]{
            0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_,
            1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_,
            0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_
        };
    }

    /**
     * Creates a Color from OkLab coordinates.
     *
     * @param L Lightness.
     * @param a The a-axis (green to red).
     * @param b The b-axis (blue to yellow).
     * @return The resulting {@link Color}.
     */
    public static Color fromOkLab(double L, double a, double b) {
        double l_ = L + 0.3963377774f * a + 0.2158037573f * b;
        double m_ = L - 0.1055613458f * a - 0.0638541728f * b;
        double s_ = L - 0.0894841775f * a - 1.2914855480f * b;

        double l = l_ * l_ * l_;
        double m = m_ * m_ * m_;
        double s = s_ * s_ * s_;

        double r = +4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s;
        double g = -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s;
        double blue = -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s;

        return Color.fromRGB(clamp((int)(r * 255)), clamp((int)(g * 255)), clamp((int)(blue * 255)));
    }

    /**
     * Converts a Color to LCH (Lightness, Chroma, Hue) coordinates.
     * This is a cylindrical representation of the Lab space.
     *
     * @param c The {@link Color} to convert.
     * @return A double array containing [Lightness, Chroma, Hue in degrees].
     */
    public static double[] toLch(Color c) {
        double[] lab = toLab(c);
        double l = lab[0];
        double chroma = Math.sqrt(lab[1] * lab[1] + lab[2] * lab[2]);
        double h = Math.atan2(lab[2], lab[1]);
        h = Math.toDegrees(h);
        if (h < 0) h += 360;
        return new double[]{l, chroma, h};
    }

    /**
     * Creates a Color from LCH coordinates.
     *
     * @param l Lightness.
     * @param c Chroma.
     * @param h Hue (in degrees, 0.0 to 360.0).
     * @return The resulting {@link Color}.
     */
    public static Color fromLch(double l, double c, double h) {
        double hRad = Math.toRadians(h);
        double a = c * Math.cos(hRad);
        double b = c * Math.sin(hRad);
        return fromLab(l, a, b);
    }

    /**
     * Linearizes sRGB components (gamma expansion).
     *
     * @param n The normalized RGB component.
     * @return The linearized value.
     */
    private static double pivotRGB(double n) {
        return (n > 0.04045) ? Math.pow((n + 0.055) / 1.055, 2.4) : n / 12.92;
    }

    /**
     * De-linearizes RGB components (gamma compression) and clamps to byte range.
     *
     * @param n The linearized RGB component.
     * @return The integer component (0-255).
     */
    private static int unpivotRGB(double n) {
        double v = (n > 0.0031308) ? 1.055 * Math.pow(n, 1 / 2.4) - 0.055 : 12.92 * n;
        return clamp((int) (v * 255));
    }

    /**
     * Helper for converting XYZ to Lab.
     *
     * @param n Component ratio.
     * @return Pivoted value.
     */
    private static double pivotXYZ(double n) {
        return (n > 0.008856) ? Math.pow(n, 1.0/3.0) : (7.787 * n) + (16.0 / 116.0);
    }

    /**
     * Helper for converting Lab back to XYZ.
     *
     * @param n Component.
     * @return Unpivoted value.
     */
    private static double unpivotXYZ(double n) {
        return (n * n * n > 0.008856) ? n * n * n : (n - 16.0 / 116.0) / 7.787;
    }

    /**
     * Clamps an integer between 0 and 255.
     *
     * @param v The value to clamp.
     * @return The clamped value.
     */
    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}