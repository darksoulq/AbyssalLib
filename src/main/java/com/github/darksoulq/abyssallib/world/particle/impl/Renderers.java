package com.github.darksoulq.abyssallib.world.particle.impl;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.particle.ParticleRenderer;
import com.github.darksoulq.abyssallib.world.particle.style.Gradient;
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

public class Renderers {
    public static class Standard implements ParticleRenderer {
        private final Particle particle;
        private final int count;
        private final double speed;
        private final Object data; 

        public Standard(Particle particle, int count, double speed, Object data) {
            this.particle = particle;
            this.count = count;
            this.speed = speed;
            this.data = data;
        }

        @Override
        public void render(Location center, List<Vector> points, List<Player> players) {
            World w = center.getWorld();
            if (w == null) return;
            for (Vector v : points) {
                Location loc = center.clone().add(v);
                if (v instanceof MotionVector mv) {
                    Vector vel = mv.getVelocity();
                    w.spawnParticle(particle, players, null,
                        loc.getX(), loc.getY(), loc.getZ(),
                        0,
                        vel.getX(), vel.getY(), vel.getZ(),
                        speed == 0 ? 1 : speed,
                        data,
                        true
                    );
                } else {
                    w.spawnParticle(particle, players, null,
                        loc.getX(), loc.getY(), loc.getZ(),
                        count, 0, 0, 0, speed, data, false
                    );
                }
            }
        }
    }

    public static class DustRenderer implements ParticleRenderer {
        private final Gradient gradient;
        private final float size;

        public DustRenderer(Gradient gradient, float size) {
            this.gradient = gradient;
            this.size = size;
        }

        @Override
        public void render(Location center, List<Vector> points, List<Player> viewers) {
            if (center.getWorld() == null || points.isEmpty()) return;

            int count = points.size();
            for (int i = 0; i < count; i++) {
                Vector offset = points.get(i);
                Location loc = center.clone().add(offset);
                double progress = (double) i / (count - 1);
                if (count == 1) progress = 0;

                Particle.DustOptions data = new Particle.DustOptions(gradient.get(progress), size);

                center.getWorld().spawnParticle(
                    Particle.DUST,
                    loc.getX(), loc.getY(), loc.getZ(),
                    1, 0, 0, 0, 0,
                    data,
                    true
                );
            }
        }
    }

    public static class ItemDisplayRenderer implements ParticleRenderer {
        private final ItemStack item;
        private final Vector3f scale;
        private final Display.Billboard billboard;
        private final List<ItemDisplay> pool = new ArrayList<>();

        public ItemDisplayRenderer(ItemStack item, float scale, Display.Billboard billboard) {
            this.item = item;
            this.scale = new Vector3f(scale);
            this.billboard = billboard;
        }

        @Override
        public void render(Location center, List<Vector> points, List<Player> players) {
            World w = center.getWorld();
            if (w == null) return;

            while (pool.size() < points.size()) {
                ItemDisplay d = w.spawn(center, ItemDisplay.class, entity -> {
                    entity.setItemStack(item);
                    entity.setBillboard(billboard);
                    entity.setInterpolationDuration(1);
                    entity.setTeleportDuration(1);
                    entity.setViewRange(1.0f);
                });
                if (players != null) {
                    d.setVisibleByDefault(false);
                    players.forEach(p -> p.showEntity(AbyssalLib.getInstance(), d));
                }
                pool.add(d);
            }
            while (pool.size() > points.size()) {
                ItemDisplay d = pool.removeLast();
                if (d.isValid()) d.remove();
            }

            for (int i = 0; i < points.size(); i++) {
                Vector offset = points.get(i);
                ItemDisplay d = pool.get(i);

                if (!d.isValid()) continue;
                if (d.getLocation().distanceSquared(center) > 0.05) {
                    d.teleport(center);
                }
                Transformation t = d.getTransformation();
                Vector3f translation = new Vector3f((float) offset.getX(), (float) offset.getY(), (float) offset.getZ());
                
                Transformation newT = new Transformation(
                        translation,
                        t.getLeftRotation(),
                        scale,
                        t.getRightRotation()
                );

                d.setTransformation(newT);
                d.setInterpolationDelay(0);
                d.setInterpolationDuration(1);
            }
        }

        @Override
        public void stop() {
            for (ItemDisplay d : pool) if (d.isValid()) d.remove();
            pool.clear();
        }
    }

    public static class ImageRenderer implements ParticleRenderer {

        private final float size;
        private final Color fallbackColor;

        /**
         * @param size The size of the dust.
         * @param fallbackColor Color to use if a point isn't a Pixel (e.g. if you mix shapes).
         */
        public ImageRenderer(float size, Color fallbackColor) {
            this.size = size;
            this.fallbackColor = fallbackColor;
        }

        public ImageRenderer(float size) {
            this(size, Color.WHITE);
        }

        @Override
        public void render(Location center, List<Vector> points, List<Player> viewers) {
            if (center.getWorld() == null || points.isEmpty()) return;
            for (Vector v : points) {
                Location loc = center.clone().add(v);
                Color c = fallbackColor;
                if (v instanceof Pixel pixel) {
                    c = pixel.getColor();
                }
                Particle.DustOptions data = new Particle.DustOptions(c, size);
                center.getWorld().spawnParticle(
                    Particle.DUST,
                    loc.getX(), loc.getY(), loc.getZ(),
                    1, 0, 0, 0, 0,
                    data,
                    true
                );
            }
        }
    }
}