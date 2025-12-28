package com.github.darksoulq.abyssallib.world.particle.style;

import org.bukkit.Color;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Pixel extends Vector {
    private final Color color;

    public Pixel(double x, double y, double z, Color color) {
        super(x, y, z);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public @NotNull Pixel clone() {
        return new Pixel(getX(), getY(), getZ(), color);
    }
}