package com.github.darksoulq.abyssallib.world.particle.old;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

/**
 * @deprecated Use {@link com.github.darksoulq.abyssallib.world.particle.Particles}
 */
@Deprecated(forRemoval = true, since = "v1.8.0-mc1.21.9")
public final class Particles {

    private static final Random RANDOM = new Random();

    private final Particle type;
    private final ItemStack displayItem;
    private final Location origin;
    private final ParticleEmitter emitter;
    private final int count;
    private final double offsetX, offsetY, offsetZ;
    private final double speed;
    private final Shape shape;
    private final float scaleX, scaleY, scaleZ;
    private final Display.Billboard billboard;
    private final float xRotDeg, yRotDeg, zRotDeg;
    private final Object data;
    private final long interval;
    private final long duration;
    private final BooleanSupplier cancelIf;
    private final List<Player> viewers;
    private final boolean asyncShape;

    private final Quaternionf precomputedRotation;
    private final LocationPool locationPool = new LocationPool();
    private final List<ItemDisplay> spawnedDisplays = new ArrayList<>();

    private BukkitTask task;

    private Particles(Builder b) {
        this.type = b.type;
        this.displayItem = b.displayItem;
        this.origin = b.origin;
        this.emitter = b.emitter;
        this.count = b.count;
        this.offsetX = b.offsetX;
        this.offsetY = b.offsetY;
        this.offsetZ = b.offsetZ;
        this.speed = b.speed;
        this.shape = b.shape;
        this.scaleX = b.scaleX;
        this.scaleY = b.scaleY;
        this.scaleZ = b.scaleZ;
        this.billboard = b.billboard;
        this.xRotDeg = b.xRotDeg;
        this.yRotDeg = b.yRotDeg;
        this.zRotDeg = b.zRotDeg;
        this.data = b.data;
        this.interval = b.interval;
        this.duration = b.duration;
        this.cancelIf = b.cancelIf;
        this.viewers = b.viewers;
        this.asyncShape = b.asyncShape;
        this.precomputedRotation = new Quaternionf()
                .rotateX((float) Math.toRadians(xRotDeg))
                .rotateY((float) Math.toRadians(yRotDeg))
                .rotateZ((float) Math.toRadians(zRotDeg));
    }

    public void start() {
        if (origin == null && emitter == null)
            throw new IllegalStateException("Origin or emitter required");
        stop();
        final long[] elapsed = {0};
        Runnable tickRunnable = () -> {
            Location base = (emitter != null ? emitter.getLocation() : origin);
            if (base == null || (cancelIf != null && cancelIf.getAsBoolean())) {
                stop();
                return;
            }
            if (asyncShape && shape != null) {
                long tickNow = elapsed[0];
                CompletableFuture
                        .supplyAsync(() -> shape.points(base, tickNow, this))
                        .thenAccept(points -> Bukkit.getScheduler().runTask(AbyssalLib.getInstance(), () -> spawnAt(points)));
            } else {
                List<Location> points = (shape != null)
                        ? shape.points(base, elapsed[0], this)
                        : randomPoints(base);
                spawnAt(points);
            }
            elapsed[0] += interval;
            if (duration > 0 && elapsed[0] >= duration) stop();
        };
        task = Bukkit.getScheduler().runTaskTimer(AbyssalLib.getInstance(), tickRunnable, 0L, interval);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (!spawnedDisplays.isEmpty()) {
            for (ItemDisplay d : spawnedDisplays) {
                if (d != null && !d.isDead()) d.remove();
            }
            spawnedDisplays.clear();
        }
        locationPool.clear();
    }

    public Location poolLocation(double x, double y, double z) {
        Location loc = locationPool.acquire(origin.getWorld());
        loc.set(x, y, z);
        return loc;
    }

    public List<Location> getLocationBuffer(int size) {
        List<Location> list = new ArrayList<>(size);
        World w = origin.getWorld();
        for (int i = 0; i < size; i++) {
            list.add(locationPool.acquire(w));
        }
        return list;
    }

    private List<Location> randomPoints(Location base) {
        List<Location> pts = new ArrayList<>(count);
        World w = base.getWorld();
        for (int i = 0; i < count; i++) {
            Location l = locationPool.acquire(w);
            l.setX(base.getX() + (RANDOM.nextDouble() - 0.5) * offsetX * 2.0);
            l.setY(base.getY() + (RANDOM.nextDouble() - 0.5) * offsetY * 2.0);
            l.setZ(base.getZ() + (RANDOM.nextDouble() - 0.5) * offsetZ * 2.0);
            pts.add(l);
        }
        return pts;
    }

    public void spawnAt(List<Location> points) {
        if (points == null || points.isEmpty()) return;
        if (displayItem == null) {
            spawnBukkitParticles(points);
        } else {
            spawnItemDisplays(points);
        }
        for (Location l : points) locationPool.release(l);
    }

    private void spawnBukkitParticles(List<Location> points) {
        if (viewers != null && !viewers.isEmpty()) {
            for (Player p : viewers) {
                for (Location pt : points) {
                    if (data != null) p.spawnParticle(type, pt, 1, offsetX, offsetY, offsetZ, speed, data);
                    else p.spawnParticle(type, pt, 1, offsetX, offsetY, offsetZ, speed);
                }
            }
        } else {
            for (Location pt : points) {
                World w = pt.getWorld();
                if (w == null) continue;
                if (data != null) w.spawnParticle(type, pt, 1, offsetX, offsetY, offsetZ, speed, data);
                else w.spawnParticle(type, pt, 1, offsetX, offsetY, offsetZ, speed);
            }
        }
    }

    private void spawnItemDisplays(List<Location> points) {
        for (ItemDisplay d : spawnedDisplays) if (d != null && !d.isDead()) d.remove();
        spawnedDisplays.clear();
        for (Location pt : points) {
            World w = pt.getWorld();
            if (w == null) continue;
            ItemDisplay d = w.spawn(pt.clone(), ItemDisplay.class);
            d.setItemStack(displayItem.clone());
            d.setBillboard(billboard);
            Transformation transform = d.getTransformation();
            transform.getScale().set(scaleX, scaleY, scaleZ);
            transform.getLeftRotation().set(precomputedRotation);
            d.setTransformation(transform);
            spawnedDisplays.add(d);
        }
        if (viewers != null && !viewers.isEmpty()) {
            for (ItemDisplay d : spawnedDisplays) {
                d.setVisibleByDefault(false);
                for (Player p : viewers) p.showEntity(AbyssalLib.getInstance(), d);
            }
        }
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Particle type;
        private ItemStack displayItem;
        private Location origin;
        private ParticleEmitter emitter;
        private int count = 1;
        private double offsetX, offsetY, offsetZ;
        private double speed;
        private Shape shape;
        private float scaleX = 1f, scaleY = 1f, scaleZ = 1f;
        private Display.Billboard billboard = Display.Billboard.HORIZONTAL;
        private float xRotDeg, yRotDeg, zRotDeg;
        private Object data;
        private long interval = 1;
        private long duration = -1;
        private BooleanSupplier cancelIf;
        private List<Player> viewers;
        private boolean asyncShape = false;

        public Builder particle(Particle p) { this.type = p; return this; }
        public Builder display(ItemStack item) { this.displayItem = item; return this; }
        public Builder spawnAt(Location loc) { this.origin = loc; return this; }
        public Builder spawnAt(ParticleEmitter e) { this.emitter = e; return this; }
        public Builder count(int c) { this.count = c; return this; }
        public Builder offset(double x, double y, double z) { this.offsetX = x; this.offsetY = y; this.offsetZ = z; return this; }
        public Builder speed(double s) { this.speed = s; return this; }
        public Builder shape(Shape s) { this.shape = s; return this; }
        public Builder scale(float s) { return scale(s, s, s); }
        public Builder scale(float x, float y, float z) { this.scaleX = x; this.scaleY = y; this.scaleZ = z; return this; }
        public Builder billboard(Display.Billboard b) { this.billboard = b; return this; }
        public Builder rotation(float xDeg, float yDeg, float zDeg) { this.xRotDeg = xDeg; this.yRotDeg = yDeg; this.zRotDeg = zDeg; return this; }
        public Builder data(Object d) { this.data = d; return this; }
        public Builder interval(long ticks) { this.interval = ticks; return this; }
        public Builder duration(long ticks) { this.duration = ticks; return this; }
        public Builder cancelIf(BooleanSupplier s) { this.cancelIf = s; return this; }
        public Builder viewers(List<Player> vs) { this.viewers = vs; return this; }
        public Builder viewers(Player single) { this.viewers = Collections.singletonList(single); return this; }
        public Builder asyncShape(boolean v) { this.asyncShape = v; return this; }

        public Particles build() {
            if (type == null && displayItem == null)
                throw new IllegalStateException("Need either particle type or display item");
            return new Particles(this);
        }
    }

    private static final class LocationPool {
        private final Deque<Location> pool = new ArrayDeque<>(64);
        Location acquire(World w) {
            Location l = pool.pollFirst();
            if (l == null) return new Location(w, 0, 0, 0);
            l.setWorld(w);
            return l;
        }
        void release(Location l) {
            if (l != null) {
                l.setWorld(null);
                pool.offerFirst(l);
            }
        }
        void clear() { pool.clear(); }
    }
}
