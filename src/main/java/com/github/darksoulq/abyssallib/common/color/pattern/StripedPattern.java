package com.github.darksoulq.abyssallib.common.color.pattern;

import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.util.Vector;

public class StripedPattern implements ColorProvider {
    private final Color c1;
    private final Color c2;
    private final double width;
    private final Vector direction;

    public StripedPattern(Color c1, Color c2, double width, Vector direction) {
        this.c1 = c1;
        this.c2 = c2;
        this.width = width;
        this.direction = direction.normalize();
    }

    @Override
    public Color get(Vector pos, double progress) {
        double proj = pos.dot(direction);
        return (Math.floor(proj / width) % 2 == 0) ? c1 : c2;
    }
}