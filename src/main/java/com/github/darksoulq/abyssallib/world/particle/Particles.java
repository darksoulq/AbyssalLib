package com.github.darksoulq.abyssallib.world.particle;

import com.github.darksoulq.abyssallib.AbyssalLib;
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
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Particles {

    private final Supplier<Location> origin;
    private final Generator generator;
    private final ParticleRenderer renderer;
    private final List<Transformer> transformers;
    private final long interval;
    private final long duration;
    private final boolean smoothen;
    private final Supplier<List<Player>> viewers;
    private final BooleanSupplier cancelIf;

    private BukkitTask task;

    private Particles(Builder b) {
        this.origin = b.origin;
        this.generator = b.generator;
        this.renderer = b.renderer;
        this.transformers = b.transformers;
        this.interval = b.interval;
        this.duration = b.duration;
        this.smoothen = b.smoothen;
        this.viewers = b.viewers;
        this.cancelIf = b.cancelIf;
    }

    public void start() {
        stop();
        Location loc = origin.get();
        if (loc != null) renderer.start(loc);

        final long[] ticks = {0};

        task = Bukkit.getScheduler().runTaskTimer(AbyssalLib.getInstance(), () -> {
            if ((duration > 0 && ticks[0] >= duration) || (cancelIf != null && cancelIf.getAsBoolean())) {
                stop();
                return;
            }

            Location center = origin.get();
            if (center == null || center.getWorld() == null) return;
            List<Vector> points = generator.generate(ticks[0]);
            List<Vector> finalPoints = new ArrayList<>(points.size());

            List<Vector> nextPoints = null;
            if (smoothen) {
                nextPoints = generator.generate(ticks[0] + interval);
            }

            for (int i = 0; i < points.size(); i++) {
                Vector v = points.get(i);
                Vector temp = v.clone();
                for (Transformer t : transformers) {
                    temp = t.transform(temp, ticks[0]);
                }
                if (smoothen && nextPoints != null && i < nextPoints.size()) {
                    Vector nextV = nextPoints.get(i).clone();
                    for (Transformer t : transformers) {
                        nextV = t.transform(nextV, ticks[0] + interval);
                    }
                    Vector velocity = nextV.subtract(temp);
                    Color c = (v instanceof Pixel p) ? p.getColor() : Color.WHITE;
                    temp = new MotionVector(temp, velocity, c);
                }
                finalPoints.add(temp);
            }
            renderer.render(center, finalPoints, viewers == null ? null : viewers.get());
            ticks[0] += interval;
        }, 0L, interval);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        renderer.stop();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Supplier<Location> origin;
        private Generator generator;
        private ParticleRenderer renderer;
        private final List<Transformer> transformers = new ArrayList<>();
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
        public Builder rotate(double xRad, double yRad, double zRad) {
            return transform((v, tick) -> v.rotateAroundX(xRad).rotateAroundY(yRad).rotateAroundZ(zRad));
        }
        public Builder scale(double s) {
            return transform((v, tick) -> v.multiply(s));
        }
        public Builder offset(double x, double y, double z) {
            return transform((v, tick) -> v.add(new Vector(x, y, z)));
        }

        public Builder interval(long i) { this.interval = i; return this; }
        public Builder duration(long d) { this.duration = d; return this; }
        public Builder smooth(boolean s) {
            this.smoothen = s;
            return this;
        }

        public Builder viewers(List<Player> players) {
            this.viewers = () -> players;
            return this;
        }

        public Builder viewers(Supplier<List<Player>> viewers) {
            this.viewers = viewers;
            return this;
        }

        public Builder stopIf(BooleanSupplier s) { this.cancelIf = s; return this; }

        public Particles build() {
            if (origin == null || generator == null || renderer == null) throw new IllegalStateException("Missing origin, generator, or renderer");
            return new Particles(this);
        }
    }
}