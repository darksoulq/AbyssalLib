package com.github.darksoulq.abyssallib.world.particle.style;

import org.bukkit.Color;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A specialized {@link Vector} that carries color information.
 * <p>
 * This class is used by the particle system to represent a single point in space
 * that has a specific tint. It allows {@link com.github.darksoulq.abyssallib.world.particle.Generator}s
 * to pass color data directly to {@link com.github.darksoulq.abyssallib.world.particle.ParticleRenderer}s.
 */
public class Pixel extends Vector {
    /** The Bukkit {@link Color} assigned to this specific coordinate. */
    private final Color color;

    /**
     * Constructs a new Pixel with the specified coordinates and color.
     *
     * @param x     The X coordinate.
     * @param y     The Y coordinate.
     * @param z     The Z coordinate.
     * @param color The {@link Color} to associate with this point.
     */
    public Pixel(double x, double y, double z, Color color) {
        super(x, y, z);
        this.color = color;
    }

    /**
     * Retrieves the color associated with this vector.
     *
     * @return The {@link Color} instance.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Creates a deep clone of this Pixel.
     *
     * @return A new {@link Pixel} instance with identical coordinates and color.
     */
    @Override
    public @NotNull Pixel clone() {
        return new Pixel(getX(), getY(), getZ(), color);
    }
}