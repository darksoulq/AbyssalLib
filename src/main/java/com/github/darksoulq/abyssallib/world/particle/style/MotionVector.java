package com.github.darksoulq.abyssallib.world.particle.style;

import org.bukkit.Color;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class MotionVector extends Pixel {
    private final Vector velocity;

    public MotionVector(Vector location, Vector velocity, Color color) {
        super(location.getX(), location.getY(), location.getZ(), color);
        this.velocity = velocity;
    }

    public MotionVector(Vector location, Vector velocity) {
        this(location, velocity, Color.WHITE);
    }

    public Vector getVelocity() {
        return velocity;
    }

    @Override
    public @NotNull MotionVector clone() {
        return new MotionVector(this, this.velocity.clone(), this.getColor());
    }
}