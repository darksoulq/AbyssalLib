package com.github.darksoulq.abyssallib.particle;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.function.Predicate;

/**
 * A fluent API for creating and managing particle effects with optional shapes, animation,
 * conditional cancellation, and scheduled repetition.
 */
public class Particles {
    private final org.bukkit.Particle type;
    private Location origin;
    private int count = 1;
    private double offsetX, offsetY, offsetZ;
    private double speed = 0;
    private Shape shape;
    private Predicate<Void> cancelIf;
    private long interval = 1;
    private long repeat = -1;

    /**
     * Creates a new particle builder with the given particle type.
     *
     * @param type the Bukkit particle type
     * @return a new {@link Particles} instance
     */
    public static Particles particle(Particle type) {
        return new Particles(type);
    }

    /**
     * Constructs a particle builder for the given particle type.
     *
     * @param type the Bukkit particle type
     */
    private Particles(org.bukkit.Particle type) {
        this.type = type;
    }

    /**
     * Sets the origin location for the particle effect.
     *
     * @param loc the origin location
     * @return this builder instance
     */
    public Particles at(Location loc) {
        this.origin = loc;
        return this;
    }

    /**
     * Sets the number of particles to spawn.
     *
     * @param count the number of particles
     * @return this builder instance
     */
    public Particles count(int count) {
        this.count = count;
        return this;
    }

    /**
     * Sets the offset values used when spawning the particles.
     *
     * @param x the X offset
     * @param y the Y offset
     * @param z the Z offset
     * @return this builder instance
     */
    public Particles offset(double x, double y, double z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        return this;
    }

    /**
     * Sets the speed of the particle effect.
     *
     * @param speed the particle speed
     * @return this builder instance
     */
    public Particles speed(double speed) {
        this.speed = speed;
        return this;
    }

    /**
     * Sets the shape used to render the particles.
     *
     * @param shape the {@link Shape} to use
     * @return this builder instance
     */
    public Particles shape(Shape shape) {
        this.shape = shape;
        return this;
    }

    /**
     * Sets the interval in ticks between each render cycle.
     *
     * @param interval the tick interval
     * @return this builder instance
     */
    public Particles everyTicks(long interval) {
        this.interval = interval;
        return this;
    }

    /**
     * Sets how long (in ticks) the particle should repeat. Set to -1 to repeat indefinitely.
     *
     * @param ticks the total duration in ticks
     * @return this builder instance
     */
    public Particles repeat(long ticks) {
        this.repeat = ticks;
        return this;
    }

    /**
     * Cancels the particle effect if the given condition returns true.
     *
     * @param condition a predicate that cancels the task if true
     * @return this builder instance
     */
    public Particles cancelIf(Predicate<Void> condition) {
        this.cancelIf = condition;
        return this;
    }

    /**
     * Starts the particle effect using Bukkit's scheduler.
     */
    public void start() {
        if (origin == null) throw new IllegalStateException("Origin must be set");

        Bukkit.getScheduler().runTaskTimer(AbyssalLib.getInstance(), new Runnable() {
            long time = 0;

            @Override
            public void run() {
                if (cancelIf != null && cancelIf.test(null)) return;

                if (shape != null) {
                    shape.animate(Particles.this, origin, time);
                    for (Location point : shape.points(origin)) {
                        origin.getWorld().spawnParticle(type, point, count, offsetX, offsetY, offsetZ, speed);
                    }
                } else {
                    origin.getWorld().spawnParticle(type, origin, count, offsetX, offsetY, offsetZ, speed);
                }

                time += interval;
                if (repeat > 0 && time >= repeat) Bukkit.getScheduler().cancelTask(this.hashCode());
            }
        }, 0, interval);
    }
}
