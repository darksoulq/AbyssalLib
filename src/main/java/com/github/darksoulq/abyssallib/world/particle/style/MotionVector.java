package com.github.darksoulq.abyssallib.world.particle.style;

import org.bukkit.Color;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * An extension of {@link Pixel} that includes a velocity vector.
 * <p>
 * This class is primarily used when the particle system's "smoothing" feature
 * is enabled. It provides the {@link com.github.darksoulq.abyssallib.world.particle.ParticleRenderer}
 * with the necessary data to spawn particles with directional movement, enabling
 * fluid client-side interpolation between animation frames.
 */
public class MotionVector extends Pixel {
    /** The velocity {@link Vector} representing the movement direction and speed. */
    private final Vector velocity;

    /**
     * Constructs a new MotionVector with a specific location, velocity, and color.
     *
     * @param location The current position {@link Vector}.
     * @param velocity The movement {@link Vector}.
     * @param color    The {@link Color} of the particle.
     */
    public MotionVector(Vector location, Vector velocity, Color color) {
        super(location.getX(), location.getY(), location.getZ(), color);
        this.velocity = velocity;
    }

    /**
     * Constructs a new MotionVector with a default color of {@link Color#WHITE}.
     *
     * @param location The current position {@link Vector}.
     * @param velocity The movement {@link Vector}.
     */
    public MotionVector(Vector location, Vector velocity) {
        this(location, velocity, Color.WHITE);
    }

    /**
     * Retrieves the velocity vector for this point.
     *
     * @return The {@link Vector} representing velocity.
     */
    public Vector getVelocity() {
        return velocity;
    }

    /**
     * Creates a deep clone of this MotionVector.
     *
     * @return A new {@link MotionVector} instance with cloned position and velocity.
     */
    @Override
    public @NotNull MotionVector clone() {
        return new MotionVector(this, this.velocity.clone(), this.getColor());
    }
}