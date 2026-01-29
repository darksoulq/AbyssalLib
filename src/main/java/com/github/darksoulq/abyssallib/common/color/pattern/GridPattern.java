package com.github.darksoulq.abyssallib.common.color.pattern;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;

public class GridPattern implements ColorProvider {
    private final Color bg;
    private final Color line;
    private final double size;
    private final double thickness;

    public GridPattern(Color bg, Color line, double size, double thickness) {
        this.bg = bg;
        this.line = line;
        this.size = size;
        this.thickness = thickness;
    }

    @Override
    public Color get(Vector pos, double progress) {
        double x = Math.abs(pos.getX()) % size;
        double z = Math.abs(pos.getZ()) % size;
        
        if (x < thickness || z < thickness) return line;
        return bg;
    }
}