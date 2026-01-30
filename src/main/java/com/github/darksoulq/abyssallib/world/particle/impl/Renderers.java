package com.github.darksoulq.abyssallib.world.particle.impl;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.particle.ParticleRenderer;
import com.github.darksoulq.abyssallib.world.particle.style.MotionVector;
import com.github.darksoulq.abyssallib.world.particle.style.Pixel;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of standard {@link ParticleRenderer} implementations.
 * <p>
 * Provides various ways to visualize points in the world, including standard
 * particles, colored dust, and high-performance entity displays.
 */
public class Renderers {

    /**
     * A standard renderer that uses Bukkit's {@link Particle} API.
     * <p>
     * Supports both static points and velocity-based points via {@link MotionVector}.
     */
    public static class Standard implements ParticleRenderer {
        /** The Bukkit particle type to spawn. */
        private final Particle particle;
        /** The number of particles to spawn per point. */
        private final int count;
        /** The speed/extra data for the particle. */
        private final double speed;
        /** Optional extra data for specific particles (e.g., BlockData). */
        private final Object data;

        /**
         * Constructs a new Standard renderer.
         *
         * @param particle The {@link Particle} type.
         * @param count    Particle count per point.
         * @param speed    Particle speed.
         * @param data     Extra particle data.
         */
        public Standard(Particle particle, int count, double speed, Object data) {
            this.particle = particle;
            this.count = count;
            this.speed = speed;
            this.data = data;
        }

        /**
         * Spawns particles for each calculated vector.
         *
         * @param center  The central origin location.
         * @param points  The list of vectors to render.
         * @param viewers The list of players who can see the particles.
         */
        @Override
        public void render(Location center, List<Vector> points, List<Player> viewers) {
            World w = center.getWorld();
            if (w == null) return;
            for (Vector v : points) {
                Location loc = center.clone().add(v);
                if (v instanceof MotionVector mv) {
                    Vector vel = mv.getVelocity();
                    w.spawnParticle(particle, viewers, null, loc.getX(), loc.getY(), loc.getZ(), 0, vel.getX(), vel.getY(), vel.getZ(), speed == 0 ? 1 : speed, data, true);
                } else {
                    w.spawnParticle(particle, viewers, null, loc.getX(), loc.getY(), loc.getZ(), count, 0, 0, 0, speed, data, false);
                }
            }
        }
    }

    /**
     * A specialized renderer for {@link Particle#DUST}.
     * <p>
     * Automatically extracts color information from {@link Pixel} vectors
     * to create high-fidelity colored effects.
     */
    public static class DustRenderer implements ParticleRenderer {
        /** The size scale of the dust particles. */
        private final float size;

        /**
         * Constructs a new DustRenderer.
         *
         * @param size The dust size (0.1 to 4.0).
         */
        public DustRenderer(float size) {
            this.size = size;
        }

        /**
         * Renders colored dust particles.
         *
         * @param center  The origin location.
         * @param points  The list of vectors, ideally instances of {@link Pixel}.
         * @param viewers The list of recipients.
         */
        @Override
        public void render(Location center, List<Vector> points, List<Player> viewers) {
            World w = center.getWorld();
            if (w == null || points.isEmpty()) return;

            for (Vector v : points) {
                Location loc = center.clone().add(v);

                Color c = Color.WHITE;
                if (v instanceof Pixel p) {
                    c = p.getColor();
                }

                w.spawnParticle(Particle.DUST, viewers, null, loc.getX(), loc.getY(), loc.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(c, size), true);
            }
        }
    }

    /**
     * A high-performance renderer that uses {@link ItemDisplay} entities.
     * <p>
     * Instead of spawning particles every tick, this renderer manages a pool of
     * entities and updates their transformations. This allows for complex 3D
     * models to be part of an effect with minimal network overhead.
     */
    public static class ItemDisplayRenderer implements ParticleRenderer {
        /** The item stack to be displayed by each entity. */
        private final ItemStack item;
        /** The scale vector for the display entities. */
        private final Vector3f scale;
        /** The billboard behavior (how the item faces the player). */
        private final Display.Billboard billboard;
        /** The internal entity pool managed by the renderer. */
        private final List<ItemDisplay> pool = new ArrayList<>();

        /**
         * Constructs a new ItemDisplayRenderer.
         *
         * @param item      The {@link ItemStack} to display.
         * @param scale     The uniform scale of the items.
         * @param billboard The {@link Display.Billboard} mode.
         */
        public ItemDisplayRenderer(ItemStack item, float scale, Display.Billboard billboard) {
            this.item = item;
            this.scale = new Vector3f(scale);
            this.billboard = billboard;
        }

        /**
         * Manages the entity pool and updates transformations.
         * <p>
         * Automatically spawns new entities if the point count increases and
         * cleans up excess entities if it decreases.
         *
         * @param center  The origin location.
         * @param points  The target coordinates for the displays.
         * @param players The players who are allowed to see these entities.
         */
        @Override
        public void render(Location center, List<Vector> points, List<Player> players) {
            World w = center.getWorld();
            if (w == null) return;

            while (pool.size() < points.size()) {
                ItemDisplay d = w.spawn(center, ItemDisplay.class, e -> {
                    e.setItemStack(item);
                    e.setBillboard(billboard);
                    e.setInterpolationDuration(1);
                    e.setTeleportDuration(1);
                    e.setViewRange(1.0f);
                    if (players != null) {
                        e.setVisibleByDefault(false);
                        players.forEach(p -> p.showEntity(AbyssalLib.getInstance(), e));
                    }
                });
                pool.add(d);
            }
            while (pool.size() > points.size()) {
                ItemDisplay d = pool.removeLast();
                if (d.isValid()) d.remove();
            }

            for (int i = 0; i < points.size(); i++) {
                ItemDisplay d = pool.get(i);
                if (!d.isValid()) continue;
                Vector offset = points.get(i);

                if (d.getLocation().distanceSquared(center) > 1) d.teleport(center);

                Transformation t = d.getTransformation();
                d.setTransformation(new Transformation(
                    new Vector3f((float)offset.getX(), (float)offset.getY(), (float)offset.getZ()),
                    t.getLeftRotation(),
                    scale,
                    t.getRightRotation()
                ));
                d.setInterpolationDelay(0);
                d.setInterpolationDuration(1);
            }
        }

        /**
         * Removes all entities in the pool and clears the list.
         */
        @Override
        public void stop() {
            pool.forEach(ItemDisplay::remove);
            pool.clear();
        }
    }
}