package com.github.darksoulq.abyssallib.common.model.blockbench.animation;

public enum BBEasing {
    LINEAR, STEP, SMOOTH, BEZIER;

    public static BBEasing parse(String type) {
        if (type == null) return LINEAR;
        return switch (type.toLowerCase()) {
            case "step" -> STEP;
            case "smooth", "catmullrom" -> SMOOTH;
            case "bezier" -> BEZIER;
            default -> LINEAR;
        };
    }
}