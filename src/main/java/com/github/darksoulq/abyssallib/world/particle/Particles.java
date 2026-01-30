package com.github.darksoulq.abyssallib.world.particle;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import com.github.darksoulq.abyssallib.world.particle.style.MotionVector;
import com.github.darksoulq.abyssallib.world.particle.style.Pixel;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * A stateful controller for managing complex, animated particle effects.
 * <p>
 * This class handles asynchronous geometric calculations, spatial transformations,
 * and thread-safe rendering synchronization. It utilizes a builder pattern for
 * modular configuration of shapes, movement, and visual styles.
 */
public class Particles {

    /** The supplier providing the central world location for the effect. */
    private final Supplier<Location> origin;
    /** The logic responsible for generating the initial coordinate set. */
    private final Generator generator;
    /** The implementation responsible for displaying the points in the world. */
    private final ParticleRenderer renderer;
    /** A list of spatial modifiers applied sequentially to generated points. */
    private final List<Transformer> transformers;
    /** The procedural color logic for per-particle tinting. */
    private final ColorProvider colorProvider;
    /** The delay in server ticks between each animation frame. */
    private final long interval;
    /** The total lifetime of the effect in ticks, or -1 for infinite. */
    private final long duration;
    /** Whether to calculate motion vectors for client-side interpolation. */
    private final boolean smoothen;
    /** The supplier for the list of players who can see the effect. */
    private final Supplier<List<Player>> viewers;
    /** A dynamic condition that, if true, forcefully terminates the effect. */
    private final BooleanSupplier cancelIf;

    /** The active Bukkit task handling the asynchronous tick loop. */
    private BukkitTask task;
    /** Thread-safe flag indicating if the effect is currently active. */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /** Thread-safe flag to prevent overlapping asynchronous processing cycles. */
    private final AtomicBoolean processing = new AtomicBoolean(false);
    /** The current age of the effect instance in ticks. */
    private final AtomicLong currentTick = new AtomicLong(0);

    /**
     * Private constructor used by the Builder.
     *
     * @param b The configuration builder.
     */
    private Particles(Builder b) {
        this.origin = b.origin;
        this.generator = b.generator;
        this.renderer = b.renderer;
        this.transformers = b.transformers;
        this.colorProvider = b.colorProvider;
        this.interval = b.interval;
        this.duration = b.duration;
        this.smoothen = b.smoothen;
        this.viewers = b.viewers;
        this.cancelIf = b.cancelIf;
    }

    /**
     * Initializes the particle effect and starts the asynchronous scheduler.
     * <p>
     * If an effect is already running on this instance, it will be stopped
     * before starting anew.
     */
    public void start() {
        stop();
        if (origin.get() == null) return;

        running.set(true);
        currentTick.set(0);
        processing.set(false);
        renderer.start(origin.get());
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(AbyssalLib.getInstance(), this::tick, 0L, interval);
    }

    /**
     * The core logic loop executed every {@link #interval}.
     * <p>
     * Performs termination checks, increments the tick counter, and manages
     * the hand-off between asynchronous calculation and synchronous rendering.
     */
    private void tick() {
        if (!running.get() || (cancelIf != null && cancelIf.getAsBoolean())) {
            stop();
            return;
        }

        long tick = currentTick.get();
        if (duration > 0 && tick >= duration) {
            stop();
            return;
        }

        if (!processing.compareAndSet(false, true)) return;

        try {
            Location center = origin.get();
            if (center == null) {
                processing.set(false);
                return;
            }

            List<Vector> points = calculateVectors(tick);
            List<Player> playerList = viewers != null ? viewers.get() : null;

            Bukkit.getScheduler().runTask(AbyssalLib.getInstance(), () -> {
                if (running.get()) {
                    renderer.render(center, points, playerList);
                }
                processing.set(false);
            });

        } catch (Exception e) {
            e.printStackTrace();
            processing.set(false);
        }

        currentTick.addAndGet(interval);
    }

    /**
     * Calculates the final spatial coordinates for a specific tick.
     * <p>
     * This method applies all {@link Transformer}s, samples the {@link ColorProvider},
     * and optionally computes motion vectors if smoothing is enabled.
     *
     * @param tick The current animation tick.
     * @return A list of processed vectors representing the frame.
     */
    private List<Vector> calculateVectors(long tick) {
        List<Vector> points = generator.generate(tick);
        if (points.isEmpty()) return points;

        List<Vector> finalPoints = new ArrayList<>(points.size());
        List<Vector> nextPoints = smoothen ? generator.generate(tick + interval) : null;
        double progress = duration > 0 ? (double) tick / duration : (tick % 100) / 100.0;

        for (int i = 0; i < points.size(); i++) {
            Vector v = points.get(i).clone();

            for (Transformer t : transformers) {
                v = t.transform(v, tick);
            }

            if (colorProvider != null) {
                Color c = colorProvider.get(v, progress);
                v = new Pixel(v.getX(), v.getY(), v.getZ(), c);
            }

            if (smoothen && nextPoints != null && i < nextPoints.size()) {
                Vector nextV = nextPoints.get(i).clone();
                for (Transformer t : transformers) {
                    nextV = t.transform(nextV, tick + interval);
                }
                Vector velocity = nextV.subtract(v);
                Color c = (v instanceof Pixel p) ? p.getColor() : Color.WHITE;
                v = new MotionVector(v, velocity, c);
            }
            finalPoints.add(v);
        }
        return finalPoints;
    }

    /**
     * Halts the effect, cancels the background task, and triggers
     * the renderer's cleanup logic.
     */
    public void stop() {
        running.set(false);
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
        Bukkit.getScheduler().runTask(AbyssalLib.getInstance(), renderer::stop);
    }

    /**
     * @return A new builder instance for configuring a particle effect.
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Fluent builder class for {@link Particles}.
     */
    public static class Builder {
        private Supplier<Location> origin;
        private Generator generator;
        private ParticleRenderer renderer;
        private final List<Transformer> transformers = new ArrayList<>();
        private ColorProvider colorProvider;
        private long interval = 1;
        private long duration = -1;
        private boolean smoothen;
        private Supplier<List<Player>> viewers = null;
        private BooleanSupplier cancelIf;

        /**
         * Sets a static location for the effect's origin.
         *
         * @param loc The world location.
         * @return This builder.
         */
        public Builder origin(Location loc) { this.origin = () -> loc; return this; }

        /**
         * Sets a dynamic supplier for the effect's origin.
         *
         * @param loc The supplier for the location.
         * @return This builder.
         */
        public Builder origin(Supplier<Location> loc) { this.origin = loc; return this; }

        /**
         * Sets the geometric shape generator for the particles.
         *
         * @param g The generator logic.
         * @return This builder.
         */
        public Builder shape(Generator g) { this.generator = g; return this; }

        /**
         * Sets the renderer responsible for displaying the particles.
         *
         * @param r The renderer implementation.
         * @return This builder.
         */
        public Builder render(ParticleRenderer r) { this.renderer = r; return this; }

        /**
         * Adds a custom transformer to the transformation pipeline.
         *
         * @param t The spatial transformer.
         * @return This builder.
         */
        public Builder transform(Transformer t) { this.transformers.add(t); return this; }

        /**
         * Sets a static color for all particles.
         *
         * @param color The Bukkit {@link Color}.
         * @return This builder.
         */
        public Builder color(Color color) { this.colorProvider = ColorProvider.fixed(color); return this; }

        /**
         * Sets a procedural color provider for dynamic particle tinting.
         *
         * @param provider The color provider.
         * @return This builder.
         */
        public Builder color(ColorProvider provider) { this.colorProvider = provider; return this; }

        /**
         * Adds a rotation transformation to the pipeline.
         *
         * @param x Rotation in radians around X.
         * @param y Rotation in radians around Y.
         * @param z Rotation in radians around Z.
         * @return This builder.
         */
        public Builder rotate(double x, double y, double z) {
            return transform((v, tick) -> v.rotateAroundX(x).rotateAroundY(y).rotateAroundZ(z));
        }

        /**
         * Adds a scaling transformation to the pipeline.
         *
         * @param s The multiplier to apply to coordinates.
         * @return This builder.
         */
        public Builder scale(double s) {
            return transform((v, tick) -> v.multiply(s));
        }

        /**
         * Adds a spatial offset transformation to the pipeline.
         *
         * @param x X offset.
         * @param y Y offset.
         * @param z Z offset.
         * @return This builder.
         */
        public Builder offset(double x, double y, double z) {
            return transform((v, tick) -> v.add(new Vector(x, y, z)));
        }

        /**
         * Sets the execution frequency.
         *
         * @param i Delay in ticks between frames.
         * @return This builder.
         */
        public Builder interval(long i) { this.interval = i; return this; }

        /**
         * Sets the total lifetime of the effect.
         *
         * @param d Duration in ticks.
         * @return This builder.
         */
        public Builder duration(long d) { this.duration = d; return this; }

        /**
         * Sets whether to calculate velocity for smooth client-side interpolation.
         *
         * @param s True for smooth, false for discrete frames.
         * @return This builder.
         */
        public Builder smooth(boolean s) { this.smoothen = s; return this; }

        /**
         * Sets a static list of players who can perceive the effect.
         *
         * @param players The list of players.
         * @return This builder.
         */
        public Builder viewers(List<Player> players) { this.viewers = () -> players; return this; }

        /**
         * Sets a dynamic supplier for viewers.
         *
         * @param viewers The player list supplier.
         * @return This builder.
         */
        public Builder viewers(Supplier<List<Player>> viewers) { this.viewers = viewers; return this; }

        /**
         * Sets a condition to stop the effect prematurely.
         *
         * @param s The boolean condition.
         * @return This builder.
         */
        public Builder stopIf(BooleanSupplier s) { this.cancelIf = s; return this; }

        /**
         * Validates the configuration and produces a Particles instance.
         *
         * @return The configured {@link Particles} instance.
         * @throws IllegalStateException If required components (origin, shape, render) are missing.
         */
        public Particles build() {
            if (origin == null || generator == null || renderer == null) throw new IllegalStateException("Missing required components");
            return new Particles(this);
        }
    }
}