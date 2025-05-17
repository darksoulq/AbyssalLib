package com.github.darksoulq.abyssallib.particle;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * A fluent API for creating and managing particle effects with optional shapes,
 * animations, conditional cancellation, and scheduled repetition.
 */
public class Particles {
    /** The Bukkit particle type to spawn. */
    private final Particle type;
    /** The origin location where the particle effect starts. */
    private Location origin;

    /** The number of particles to spawn each iteration. */
    private int count = 1;
    /** The X, Y, and Z offsets applied when spawning particles. */
    private double offsetX = 0, offsetY = 0, offsetZ = 0;
    /** The speed applied to the particles. */
    private double speed = 0;

    /** An optional shape that defines spatial placement of the particles. */
    private Shape shape;
    /** Optional additional data for the particle (e.g., color). */
    private Object data = null;

    /** The number of ticks between each particle spawn cycle. */
    private long interval = 1;
    /** The total duration in ticks to run this effect (-1 for infinite). */
    private long duration = -1;
    /** A condition that cancels the particle effect if it returns true. */
    private Predicate<Void> cancelIf = null;

    /**
     * Creates a new {@link Particles} builder using the specified Bukkit particle type.
     *
     * @param type the Bukkit particle type
     * @return a new {@code Particles} instance
     */
    public static Particles of(Particle type) {
        return new Particles(type);
    }

    /**
     * Private constructor used by {@link #of(Particle)}.
     *
     * @param type the Bukkit particle type
     */
    private Particles(Particle type) {
        this.type = type;
    }

    /**
     * Sets the origin location where the particle effect should start.
     *
     * @param loc the origin location
     * @return this builder instance
     */
    public Particles spawnAt(Location loc) {
        this.origin = loc;
        return this;
    }

    /**
     * Sets the number of particles to spawn per iteration.
     *
     * @param count the number of particles
     * @return this builder instance
     */
    public Particles withCount(int count) {
        this.count = count;
        return this;
    }

    /**
     * Sets the offset values for particle dispersion.
     *
     * @param x offset on the X-axis
     * @param y offset on the Y-axis
     * @param z offset on the Z-axis
     * @return this builder instance
     */
    public Particles withOffset(double x, double y, double z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        return this;
    }

    /**
     * Sets the speed for the particles.
     *
     * @param speed the speed value
     * @return this builder instance
     */
    public Particles withSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    /**
     * Applies a shape to this particle effect, controlling its spatial layout.
     *
     * @param shape the shape to use
     * @return this builder instance
     */
    public Particles usingShape(Shape shape) {
        this.shape = shape;
        return this;
    }

    /**
     * Adds optional custom data to the particles (e.g., {@code DustOptions}, {@code MaterialData}).
     *
     * @param data the particle data object
     * @return this builder instance
     */
    public Particles withData(Object data) {
        this.data = data;
        return this;
    }

    /**
     * Sets the interval (in ticks) between each particle spawn cycle.
     *
     * @param ticks interval in ticks
     * @return this builder instance
     */
    public Particles every(long ticks) {
        this.interval = ticks;
        return this;
    }

    /**
     * Sets the total duration (in ticks) this particle effect should run.
     * Set to -1 to run indefinitely.
     *
     * @param ticks total duration in ticks
     * @return this builder instance
     */
    public Particles duration(long ticks) {
        this.duration = ticks;
        return this;
    }

    /**
     * Cancels the particle effect if the given condition returns true.
     * This is checked every cycle.
     *
     * @param condition a condition to cancel the effect
     * @return this builder instance
     */
    public Particles cancelIf(Predicate<Void> condition) {
        this.cancelIf = condition;
        return this;
    }

    /**
     * Starts the particle effect using Bukkit's scheduler.
     * This will repeat at the configured interval until canceled or the duration expires.
     *
     * @throws IllegalStateException if no origin is set
     */
    public void start() {
        if (origin == null)
            throw new IllegalStateException("Origin must be set before starting the particle effect.");

        final long[] elapsed = {0};
        final AtomicInteger taskId = new AtomicInteger();

        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(AbyssalLib.getInstance(), () -> {
            if (cancelIf != null && cancelIf.test(null)) {
                Bukkit.getScheduler().cancelTask(taskId.get());
                return;
            }

            if (shape != null) {
                shape.animate(this, origin, elapsed[0]);
                for (Location point : shape.points(origin)) {
                    spawnParticle(point);
                }
            } else {
                spawnParticle(origin);
            }

            elapsed[0] += interval;

            if (duration > 0 && elapsed[0] >= duration) {
                Bukkit.getScheduler().cancelTask(taskId.get());
            }

        }, 0, interval));
    }

    /**
     * Internally spawns the particle at the given location using the configured parameters.
     *
     * @param loc the location to spawn the particle
     */
    private void spawnParticle(Location loc) {
        if (data != null) {
            loc.getWorld().spawnParticle(type, loc, count, offsetX, offsetY, offsetZ, speed, data);
        } else {
            loc.getWorld().spawnParticle(type, loc, count, offsetX, offsetY, offsetZ, speed);
        }
    }
}
