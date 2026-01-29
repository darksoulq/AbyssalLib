package com.github.darksoulq.abyssallib.world.particle.impl;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import com.github.darksoulq.abyssallib.common.color.gradient.AbstractGradient;
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

    public static class DustRenderer implements ParticleRenderer {
        private final ColorProvider provider;
        private final float size;

        public DustRenderer(ColorProvider provider, float size) {
            this.provider = provider;
            this.size = size;
        }

        @Override
        public void render(Location center, List<Vector> points, List<Player> viewers) {
            World w = center.getWorld();
            if (w == null || points.isEmpty()) return;

            for (int i = 0; i < points.size(); i++) {
                Vector v = points.get(i);
                Location loc = center.clone().add(v);

                Color c = Color.WHITE;
                if (v instanceof Pixel p) {
                    c = p.getColor();
                } else if (provider instanceof AbstractGradient gp) {
                    double progress = (double) i / Math.max(1, (points.size() - i));
                    c = gp.getAt(progress);
                } else if (provider != null) {
                    c = provider.get(v, 0);
                }

                w.spawnParticle(Particle.DUST, viewers, null, loc.getX(), loc.getY(), loc.getZ(), 1, 0, 0, 0, 0, new Particle.DustOptions(c, size), true);
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

        @Override
        public void stop() {
            pool.forEach(ItemDisplay::remove);
            pool.clear();
        }
    }
}