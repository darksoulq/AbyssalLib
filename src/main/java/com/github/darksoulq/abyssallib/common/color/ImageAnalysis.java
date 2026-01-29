package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

public class ImageAnalysis {

    public static ColorPalette extractDominantColors(BufferedImage image, int k) {
        List<Color> pixels = samplePixels(image, 1000); 
        if (pixels.isEmpty()) return new ColorPalette(Color.WHITE);
        return new ColorPalette(kMeans(pixels, k));
    }

    public static Color getAverageColor(BufferedImage image) {
        long r = 0, g = 0, b = 0, count = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                if (((rgb >> 24) & 0xFF) == 0) continue; 
                r += (rgb >> 16) & 0xFF;
                g += (rgb >> 8) & 0xFF;
                b += rgb & 0xFF;
                count++;
            }
        }
        if (count == 0) return Color.WHITE;
        return Color.fromRGB((int) (r / count), (int) (g / count), (int) (b / count));
    }

    public static ColorPalette getHistogramPalette(BufferedImage image, int bins) {
        Map<Integer, Integer> frequency = new HashMap<>();
        int step = Math.max(1, (image.getWidth() * image.getHeight()) / 2000);

        for (int x = 0; x < image.getWidth(); x += step) {
            for (int y = 0; y < image.getHeight(); y += step) {
                int rgb = image.getRGB(x, y);
                if (((rgb >> 24) & 0xFF) < 128) continue;
                int quantized = quantize(rgb, bins);
                frequency.merge(quantized, 1, Integer::sum);
            }
        }

        List<Color> colors = frequency.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(16)
                .map(e -> Color.fromRGB(e.getKey() & 0xFFFFFF))
                .collect(Collectors.toList());

        return new ColorPalette(colors);
    }

    private static List<Color> samplePixels(BufferedImage image, int maxSamples) {
        List<Color> pixels = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();
        int step = Math.max(1, (width * height) / maxSamples);

        for (int x = 0; x < width; x += step) {
            for (int y = 0; y < height; y += step) {
                int rgb = image.getRGB(x, y);
                if (((rgb >> 24) & 0xFF) > 10) { 
                    pixels.add(Color.fromRGB(rgb & 0xFFFFFF));
                }
            }
        }
        return pixels;
    }

    private static List<Color> kMeans(List<Color> pixels, int k) {
        List<Color> centroids = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            centroids.add(pixels.get(random.nextInt(pixels.size())));
        }

        for (int iter = 0; iter < 10; iter++) {
            Map<Color, List<Color>> clusters = new HashMap<>();
            for (Color p : pixels) {
                Color best = centroids.get(0);
                double minDst = ColorUtils.distanceSq(p, best);
                for (Color c : centroids) {
                    double dst = ColorUtils.distanceSq(p, c);
                    if (dst < minDst) {
                        minDst = dst;
                        best = c;
                    }
                }
                clusters.computeIfAbsent(best, x -> new ArrayList<>()).add(p);
            }

            List<Color> newCentroids = new ArrayList<>();
            for (Color c : centroids) {
                List<Color> cluster = clusters.get(c);
                if (cluster == null || cluster.isEmpty()) {
                    newCentroids.add(c); 
                } else {
                    newCentroids.add(ColorUtils.average(cluster));
                }
            }
            centroids = newCentroids;
        }
        return centroids;
    }

    private static int quantize(int rgb, int bins) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int binSize = 256 / bins;
        r = (r / binSize) * binSize;
        g = (g / binSize) * binSize;
        b = (b / binSize) * binSize;
        return (r << 16) | (g << 8) | b;
    }
}