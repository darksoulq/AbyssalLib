package com.github.darksoulq.abyssallib.common.color.gradient;

import org.bukkit.Color;
import org.bukkit.util.Vector;

public class SweepGradient extends AbstractGradient {
    public SweepGradient(Color... colors) {
        super(colors);
    }

    @Override
    public Color get(Vector position, double progress) {
        double angle = Math.atan2(position.getZ(), position.getX());
        double t = (angle + Math.PI) / (2 * Math.PI); 
        return getAt(t);
    }
}