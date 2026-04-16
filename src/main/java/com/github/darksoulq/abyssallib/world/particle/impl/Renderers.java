package com.github.darksoulq.abyssallib.world.particle.impl;

import com.github.darksoulq.abyssallib.world.particle.ParticleRenderer;
import com.github.darksoulq.abyssallib.world.particle.style.MotionVector;
import com.github.darksoulq.abyssallib.world.particle.style.Pixel;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A collection of standard {@link ParticleRenderer} implementations.
 * <p>
 * Provides various ways to visualize points in the world, including standard
 * particles, colored dust, and high-performance entity displays.
 */
public class Renderers {

    private static final int MAX_BUNDLE_SIZE = 1000;

    private static void sendBundled(List<Player> viewers, List<Packet<? super ClientGamePacketListener>> packets) {
        if (packets.isEmpty()) return;

        List<Packet<? super ClientGamePacketListener>> batch = new ArrayList<>(Math.min(packets.size(), MAX_BUNDLE_SIZE));
        for (Packet<? super ClientGamePacketListener> packet : packets) {
            batch.add(packet);
            if (batch.size() >= MAX_BUNDLE_SIZE) {
                ClientboundBundlePacket bundle = new ClientboundBundlePacket(batch);
                for (Player p : viewers) {
                    ((CraftPlayer) p).getHandle().connection.send(bundle);
                }
                batch = new ArrayList<>(Math.min(packets.size(), MAX_BUNDLE_SIZE));
            }
        }

        if (!batch.isEmpty()) {
            ClientboundBundlePacket bundle = new ClientboundBundlePacket(batch);
            for (Player p : viewers) {
                ((CraftPlayer) p).getHandle().connection.send(bundle);
            }
        }
    }

    /**
     * A standard renderer that uses Bukkit's {@link Particle} API.
     * <p>
     * Supports both static points and velocity-based points via {@link MotionVector}.
     */
    public static class Standard implements ParticleRenderer {
        private final Particle particle;
        private final int count;
        private final double speed;
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
            if (viewers == null || viewers.isEmpty() || points.isEmpty() || center.getWorld() == null) return;

            ParticleOptions options;
            try {
                options = CraftParticle.createParticleParam(particle, data);
            } catch (Throwable t) {
                return;
            }

            List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>(points.size());
            float s = (float) speed;

            for (Vector v : points) {
                double x = center.getX() + v.getX();
                double y = center.getY() + v.getY();
                double z = center.getZ() + v.getZ();

                if (v instanceof MotionVector mv) {
                    Vector vel = mv.getVelocity();
                    packets.add(new ClientboundLevelParticlesPacket(
                        options, true, false, x, y, z, (float) vel.getX(), (float) vel.getY(), (float) vel.getZ(), s == 0 ? 1f : s, 0
                    ));
                } else {
                    packets.add(new ClientboundLevelParticlesPacket(
                        options, true, false, x, y, z, 0f, 0f, 0f, s, count
                    ));
                }
            }

            sendBundled(viewers, packets);
        }
    }

    /**
     * A specialized renderer for {@link Particle#DUST}.
     * <p>
     * Automatically extracts color information from {@link Pixel} vectors
     * to create high-fidelity colored effects.
     */
    public static class DustRenderer implements ParticleRenderer {
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
            if (viewers == null || viewers.isEmpty() || points.isEmpty() || center.getWorld() == null) return;

            List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>(points.size());

            for (Vector v : points) {
                double x = center.getX() + v.getX();
                double y = center.getY() + v.getY();
                double z = center.getZ() + v.getZ();

                Color c = Color.WHITE;
                if (v instanceof Pixel p) {
                    c = p.getColor();
                }

                int colorInt = (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();
                DustParticleOptions options = new DustParticleOptions(colorInt, size);

                packets.add(new ClientboundLevelParticlesPacket(
                    options, true, false, x, y, z, 0f, 0f, 0f, 0f, 1
                ));
            }

            sendBundled(viewers, packets);
        }
    }

    /**
     * A high-performance renderer that uses {@link org.bukkit.entity.ItemDisplay} entities.
     * <p>
     * Instead of spawning particles every tick, this renderer manages a pool of
     * entities and updates their transformations. This allows for complex 3D
     * models to be part of an effect with minimal network overhead.
     */
    public static class ItemDisplayRenderer implements ParticleRenderer {
        private final ItemStack item;
        private final Vector3f scale;
        private final org.bukkit.entity.Display.Billboard billboard;
        private final List<Display.ItemDisplay> pool = new ArrayList<>();
        private final Set<UUID> viewersCache = new HashSet<>();

        /**
         * Constructs a new ItemDisplayRenderer.
         *
         * @param item      The {@link ItemStack} to display.
         * @param scale     The uniform scale of the items.
         * @param billboard The {@link org.bukkit.entity.Display.Billboard} mode.
         */
        public ItemDisplayRenderer(ItemStack item, float scale, org.bukkit.entity.Display.Billboard billboard) {
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
            if (w == null || players == null || players.isEmpty() || points.isEmpty()) return;
            ServerLevel level = ((CraftWorld) w).getHandle();

            List<Player> newViewers = new ArrayList<>();
            Set<UUID> currentIds = new HashSet<>();
            for (Player p : players) {
                currentIds.add(p.getUniqueId());
                if (!viewersCache.contains(p.getUniqueId())) {
                    newViewers.add(p);
                }
            }

            List<Player> removedViewers = new ArrayList<>();
            for (UUID id : viewersCache) {
                if (!currentIds.contains(id)) {
                    Player p = Bukkit.getPlayer(id);
                    if (p != null) removedViewers.add(p);
                }
            }

            viewersCache.clear();
            viewersCache.addAll(currentIds);

            List<Packet<? super ClientGamePacketListener>> globalPackets = new ArrayList<>();

            if (pool.size() > points.size()) {
                IntList toRemove = new IntArrayList();
                while (pool.size() > points.size()) {
                    toRemove.add(pool.remove(pool.size() - 1).getId());
                }
                globalPackets.add(new ClientboundRemoveEntitiesPacket(toRemove));
            }

            while (pool.size() < points.size()) {
                Display.ItemDisplay nms = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
                nms.setPos(center.getX(), center.getY(), center.getZ());

                org.bukkit.entity.ItemDisplay bukkit = (org.bukkit.entity.ItemDisplay) nms.getBukkitEntity();
                bukkit.setItemStack(item);
                bukkit.setBillboard(billboard);
                bukkit.setInterpolationDuration(3);
                bukkit.setTeleportDuration(3);
                bukkit.setViewRange(1.0f);

                pool.add(nms);
                globalPackets.add(new ClientboundAddEntityPacket(nms, 0, nms.blockPosition()));
            }

            for (int i = 0; i < points.size(); i++) {
                Display.ItemDisplay nms = pool.get(i);
                nms.tickCount++;
                org.bukkit.entity.ItemDisplay bukkit = (org.bukkit.entity.ItemDisplay) nms.getBukkitEntity();
                Vector offset = points.get(i);

                if (nms.distanceToSqr(center.getX(), center.getY(), center.getZ()) > 1) {
                    nms.setPos(center.getX(), center.getY(), center.getZ());
                    PositionMoveRotation pos = new PositionMoveRotation(new Vec3(center.getX(), center.getY(), center.getZ()), Vec3.ZERO, 0f, 0f);
                    globalPackets.add(ClientboundTeleportEntityPacket.teleport(nms.getId(), pos, Set.of(), false));
                }

                Transformation t = bukkit.getTransformation();
                bukkit.setTransformation(new Transformation(
                    new Vector3f((float) offset.getX(), (float) offset.getY(), (float) offset.getZ()),
                    t.getLeftRotation(),
                    scale,
                    t.getRightRotation()
                ));

                bukkit.setInterpolationDelay(0);
                bukkit.setInterpolationDuration(3);
                bukkit.setTeleportDuration(3);

                var dirty = nms.getEntityData().packDirty();
                if (dirty != null) {
                    globalPackets.add(new ClientboundSetEntityDataPacket(nms.getId(), dirty));
                }
            }

            if (!removedViewers.isEmpty() && !pool.isEmpty()) {
                IntList toRemove = new IntArrayList();
                for (Display.ItemDisplay e : pool) toRemove.add(e.getId());
                ClientboundRemoveEntitiesPacket removePacket = new ClientboundRemoveEntitiesPacket(toRemove);
                for (Player p : removedViewers) {
                    ((CraftPlayer) p).getHandle().connection.send(removePacket);
                }
            }

            for (Player p : players) {
                if (newViewers.contains(p)) {
                    List<Packet<? super ClientGamePacketListener>> initialPackets = new ArrayList<>();
                    for (Display.ItemDisplay nms : pool) {
                        initialPackets.add(new ClientboundAddEntityPacket(nms, 0, nms.blockPosition()));
                        var data = nms.getEntityData().getNonDefaultValues();
                        if (data != null) {
                            initialPackets.add(new ClientboundSetEntityDataPacket(nms.getId(), data));
                        }
                    }
                    sendBundled(List.of(p), initialPackets);
                } else {
                    sendBundled(List.of(p), globalPackets);
                }
            }
        }

        /**
         * Removes all entities in the pool and clears the list.
         */
        @Override
        public void stop() {
            if (pool.isEmpty() || viewersCache.isEmpty()) {
                pool.clear();
                viewersCache.clear();
                return;
            }

            IntList toRemove = new IntArrayList();
            for (Display.ItemDisplay e : pool) toRemove.add(e.getId());
            ClientboundRemoveEntitiesPacket removePacket = new ClientboundRemoveEntitiesPacket(toRemove);

            for (UUID id : viewersCache) {
                Player p = Bukkit.getPlayer(id);
                if (p != null && p.isOnline()) {
                    ((CraftPlayer) p).getHandle().connection.send(removePacket);
                }
            }

            pool.clear();
            viewersCache.clear();
        }
    }
}