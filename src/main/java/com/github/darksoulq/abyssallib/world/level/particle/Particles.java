package com.github.darksoulq.abyssallib.world.level.particle;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * A fluent API for creating and managing particle effects with optional shapes,
 * animations, conditional cancellation, dynamic origins, viewers, and scheduled repetition.
 */
public class Particles {
    /** Private Random reference */
    private final Random random = new Random();

    /** The Bukkit particle type to spawn. */
    private final Particle type;

    /** The Item to spawn, conflicts with Particle */
    private final Item display;

    /** The static origin location where the particle effect starts, used if emitter is not set. */
    private Location origin;

    /** A dynamic origin provider that overrides the static location if set. */
    private ParticleEmitter emitter;

    /** The number of particles to spawn each iteration. */
    private int count = 1;

    /** The X, Y, and Z offsets applied when spawning particles. */
    private double offsetX = 0, offsetY = 0, offsetZ = 0;

    /** The speed applied to the particles. */
    private double speed = 0;

    /** An optional shape that defines spatial placement of the particles. */
    private Shape shape;

    /** Optional additional data for the particle (e.g., color, block type). */
    private Object data = null;

    /** The number of ticks between each particle spawn cycle. */
    private long interval = 1;

    /** The total duration in ticks to run this effect (-1 for infinite). */
    private long duration = -1;

    /** A condition that cancels the particle effect if it returns true. */
    private Predicate<Void> cancelIf = null;

    /** A list of players to show the particle to. If null or empty, show to all nearby players. */
    private List<Player> viewers = null;

    /** A list of spawned ItemDisplays, only applicable if Item is used */
    private final List<ItemDisplay> displays = new ArrayList<>();

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
     * Creates a new {@link Particles} builder using the specified Bukkit particle type.
     *
     * @param displayItem the Item to spawn
     * @return a new {@code Particles} instance
     */
    public static Particles of(Item displayItem) {
        return new Particles(displayItem);
    }

    /**
     * Private constructor used by {@link #of(Particle)}.
     *
     * @param type the Bukkit particle type
     */
    private Particles(Particle type) {
        this.type = type;
        this.display = null;
    }

    /**
     * Private constructor used by {@link #of(Particle)}.
     *
     * @param display the ItemDisplay to use as a psrticle
     */
    private Particles(Item display) {
        this.type = null;
        this.display = display;
    }

    /**
     * Sets a static origin location for the particle effect.
     * If a {@link ParticleEmitter} is also set, it takes precedence over this.
     *
     * @param loc the origin location
     * @return this builder instance
     */
    public Particles spawnAt(Location loc) {
        this.origin = loc;
        return this;
    }

    /**
     * Sets a dynamic origin provider that will be queried every tick.
     *
     * @param emitter the dynamic location provider
     * @return this builder instance
     */
    public Particles spawnFrom(ParticleEmitter emitter) {
        this.emitter = emitter;
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
     * Sets the particle to only be visible to a specific player.
     *
     * @param player the player who will see the particles
     * @return this builder instance
     */
    public Particles withViewer(Player player) {
        this.viewers = Collections.singletonList(player);
        return this;
    }

    /**
     * Sets the particle to be visible to a list of players.
     *
     * @param players list of players who will see the particles
     * @return this builder instance
     */
    public Particles withViewers(List<Player> players) {
        this.viewers = players;
        return this;
    }

    /**
     * Starts the particle effect using Bukkit's scheduler.
     * This will repeat at the configured interval until canceled or the duration expires.
     *
     * @throws IllegalStateException if no origin or emitter is set
     */
    public void start() {
        if (origin == null && emitter == null)
            throw new IllegalStateException("Origin or ParticleEmitter must be set before starting the particle effect.");

        final long[] elapsed = {0};
        final AtomicInteger taskId = new AtomicInteger();

        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(AbyssalLib.getInstance(), () -> {
            Location loc = (emitter != null) ? emitter.getLocation() : origin;

            if (loc == null || (cancelIf != null && cancelIf.test(null))) {
                Bukkit.getScheduler().cancelTask(taskId.get());
                return;
            }

            if (display == null) {
                if (shape != null) {
                    shape.animate(this, loc, elapsed[0]);
                    for (Location point : shape.points(loc)) {
                        spawnParticle(point);
                    }
                } else {
                    spawnParticle(loc);
                }
            } else {
                if (shape != null) {
                    shape.animate(this, loc, elapsed[0]);
                    spawnDisplayParticle(loc, true);
                } else {
                    spawnDisplayParticle(loc, false);
                }
            }

            elapsed[0] += interval;

            if (duration > 0 && elapsed[0] >= duration) {
                Bukkit.getScheduler().cancelTask(taskId.get());
            }

        }, 0, interval));
    }

    /**
     * Internally spawns the particle at the given location using the configured parameters.
     * If a viewer list is set, particles are sent only to them.
     *
     * @param loc the base location to spawn the particle
     * @param shaped whether the particle uses a defined shape
     */
    private void spawnDisplayParticle(Location loc, boolean shaped) {
        if (!displays.isEmpty()) {
            displays.forEach(Entity::remove);
            displays.clear();
        }

        List<Location> spawnPoints = new ArrayList<>();

        if (shaped) {
            spawnPoints.addAll(shape.points(loc));
        } else {
            for (int i = 0; i < count; i++) {
                double x = loc.getX() + (random.nextDouble() - 0.5) * offsetX * 2;
                double y = loc.getY() + (random.nextDouble() - 0.5) * offsetY * 2;
                double z = loc.getZ() + (random.nextDouble() - 0.5) * offsetZ * 2;
                spawnPoints.add(new Location(loc.getWorld(), x, y, z));
            }
        }

        for (Location point : spawnPoints) {
            ItemDisplay displayEntity = point.getWorld().spawn(point.clone(), ItemDisplay.class);
            displayEntity.setItemStack(display.stack());
            displayEntity.setBillboard(Display.Billboard.HORIZONTAL);
            displays.add(displayEntity);
        }

        if (!viewers.isEmpty()) {
            for (ItemDisplay displayEntity : displays) {
                displayEntity.setVisibleByDefault(false);
                displayEntity.spawnAt(displayEntity.getLocation());
                viewers.forEach(player -> player.showEntity(AbyssalLib.getInstance(), displayEntity));
            }
        } else {
            displays.forEach(displayEntity -> displayEntity.spawnAt(displayEntity.getLocation()));
        }
    }


    /**
     * Internally spawns the particle at the given location using the configured parameters.
     * If a viewer list is set, particles are sent only to them.
     *
     * @param loc the location to spawn the particle
     */
    private void spawnParticle(Location loc) {
        if (viewers != null && !viewers.isEmpty()) {
            for (Player viewer : viewers) {
                if (data != null) {
                    viewer.spawnParticle(type, loc, count, offsetX, offsetY, offsetZ, speed, data);
                } else {
                    viewer.spawnParticle(type, loc, count, offsetX, offsetY, offsetZ, speed);
                }
            }
        } else {
            if (data != null) {
                loc.getWorld().spawnParticle(type, loc, count, offsetX, offsetY, offsetZ, speed, data);
            } else {
                loc.getWorld().spawnParticle(type, loc, count, offsetX, offsetY, offsetZ, speed);
            }
        }
    }
}
