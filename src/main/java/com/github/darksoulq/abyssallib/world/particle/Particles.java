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

public class Particles {

    private final Supplier<Location> origin;
    private final Generator generator;
    private final ParticleRenderer renderer;
    private final List<Transformer> transformers;
    private final ColorProvider colorProvider;
    private final long interval;
    private final long duration;
    private final boolean smoothen;
    private final Supplier<List<Player>> viewers;
    private final BooleanSupplier cancelIf;

    private BukkitTask task;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private final AtomicLong currentTick = new AtomicLong(0);

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

    public void start() {
        stop();
        if (origin.get() == null) return;

        running.set(true);
        currentTick.set(0);
        processing.set(false);
        renderer.start(origin.get());
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(AbyssalLib.getInstance(), this::tick, 0L, interval);
    }

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

    public void stop() {
        running.set(false);
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
        Bukkit.getScheduler().runTask(AbyssalLib.getInstance(), renderer::stop);
    }

    public static Builder builder() { return new Builder(); }

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

        public Builder origin(Location loc) { this.origin = () -> loc; return this; }
        public Builder origin(Supplier<Location> loc) { this.origin = loc; return this; }
        public Builder shape(Generator g) { this.generator = g; return this; }
        public Builder render(ParticleRenderer r) { this.renderer = r; return this; }
        public Builder transform(Transformer t) { this.transformers.add(t); return this; }

        public Builder color(Color color) { this.colorProvider = ColorProvider.fixed(color); return this; }
        public Builder color(ColorProvider provider) { this.colorProvider = provider; return this; }

        public Builder rotate(double x, double y, double z) {
            return transform((v, tick) -> v.rotateAroundX(x).rotateAroundY(y).rotateAroundZ(z));
        }
        public Builder scale(double s) {
            return transform((v, tick) -> v.multiply(s));
        }
        public Builder offset(double x, double y, double z) {
            return transform((v, tick) -> v.add(new Vector(x, y, z)));
        }

        public Builder interval(long i) { this.interval = i; return this; }
        public Builder duration(long d) { this.duration = d; return this; }
        public Builder smooth(boolean s) { this.smoothen = s; return this; }

        public Builder viewers(List<Player> players) { this.viewers = () -> players; return this; }
        public Builder viewers(Supplier<List<Player>> viewers) { this.viewers = viewers; return this; }
        public Builder stopIf(BooleanSupplier s) { this.cancelIf = s; return this; }

        public Particles build() {
            if (origin == null || generator == null || renderer == null) throw new IllegalStateException("Missing required components");
            return new Particles(this);
        }
    }
}