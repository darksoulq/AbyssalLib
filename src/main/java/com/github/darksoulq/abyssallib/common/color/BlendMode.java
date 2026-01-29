package com.github.darksoulq.abyssallib.common.color;

import org.bukkit.Color;

public enum BlendMode {
    NORMAL {
        @Override public Color blend(Color b, Color s) { return s; }
    },
    MULTIPLY {
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB((b.getRed() * s.getRed()) / 255, (b.getGreen() * s.getGreen()) / 255, (b.getBlue() * s.getBlue()) / 255);
        }
    },
    SCREEN {
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(255 - ((255 - b.getRed()) * (255 - s.getRed())) / 255,
                                 255 - ((255 - b.getGreen()) * (255 - s.getGreen())) / 255,
                                 255 - ((255 - b.getBlue()) * (255 - s.getBlue())) / 255);
        }
    },
    OVERLAY {
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(overlay(b.getRed(), s.getRed()), overlay(b.getGreen(), s.getGreen()), overlay(b.getBlue(), s.getBlue()));
        }
        private int overlay(int b, int s) {
            return b < 128 ? (2 * b * s / 255) : (255 - 2 * (255 - b) * (255 - s) / 255);
        }
    },
    DARKEN {
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(Math.min(b.getRed(), s.getRed()), Math.min(b.getGreen(), s.getGreen()), Math.min(b.getBlue(), s.getBlue()));
        }
    },
    LIGHTEN {
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(Math.max(b.getRed(), s.getRed()), Math.max(b.getGreen(), s.getGreen()), Math.max(b.getBlue(), s.getBlue()));
        }
    },
    ADD {
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(Math.min(255, b.getRed() + s.getRed()), Math.min(255, b.getGreen() + s.getGreen()), Math.min(255, b.getBlue() + s.getBlue()));
        }
    },
    SUBTRACT {
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(Math.max(0, b.getRed() - s.getRed()), Math.max(0, b.getGreen() - s.getGreen()), Math.max(0, b.getBlue() - s.getBlue()));
        }
    },
    DIFFERENCE {
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(Math.abs(b.getRed() - s.getRed()), Math.abs(b.getGreen() - s.getGreen()), Math.abs(b.getBlue() - s.getBlue()));
        }
    },
    EXCLUSION {
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB(
                b.getRed() + s.getRed() - 2 * b.getRed() * s.getRed() / 255,
                b.getGreen() + s.getGreen() - 2 * b.getGreen() * s.getGreen() / 255,
                b.getBlue() + s.getBlue() - 2 * b.getBlue() * s.getBlue() / 255
            );
        }
    },
    AVERAGE {
        @Override public Color blend(Color b, Color s) {
            return Color.fromRGB((b.getRed() + s.getRed()) / 2, (b.getGreen() + s.getGreen()) / 2, (b.getBlue() + s.getBlue()) / 2);
        }
    };

    public abstract Color blend(Color base, Color source);
}