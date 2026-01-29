package com.github.darksoulq.abyssallib.common.color.pattern;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;

public class CheckeredPattern implements ColorProvider {
    private final Color c1;
    private final Color c2;
    private final double size;

    public CheckeredPattern(Color c1, Color c2, double size) {
        this.c1 = c1;
        this.c2 = c2;
        this.size = size;
    }

    @Override
    public Color get(Vector pos, double progress) {
        int x = (int) Math.floor(pos.getX() / size);
        int y = (int) Math.floor(pos.getY() / size);
        int z = (int) Math.floor(pos.getZ() / size);
        return ((x + y + z) % 2 == 0) ? c1 : c2;
    }
}