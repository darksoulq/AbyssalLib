package com.github.darksoulq.abyssallib.world.multiblock.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.sql.Database;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.util.TaskUtil;
import com.github.darksoulq.abyssallib.world.multiblock.Multiblock;
import com.github.darksoulq.abyssallib.world.multiblock.MultiblockEntity;
import com.github.darksoulq.abyssallib.world.multiblock.RelativeBlockPos;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MultiblockManager {
    private static Database DATABASE;

    private static final Map<BlockKey, Multiblock> ORIGINS = new ConcurrentHashMap<>();
    private static final Map<BlockKey, Multiblock> BLOCK_CACHE = new ConcurrentHashMap<>();
    private static final Set<Multiblock> ACTIVE = ConcurrentHashMap.newKeySet();

    public static void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int saved = MultiblockManager.save();
                AbyssalLib.LOGGER.info("Saved " + saved + " multiblocks");
            }
        }.runTaskTimerAsynchronously(AbyssalLib.getInstance(), 20L * 60 * 2, 20L * 60 * 5);

        try {
            DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "multiblocks.db"));
            DATABASE.connect();

            DATABASE.executor().table("multiblocks").create()
                .ifNotExists()
                .column("world", "TEXT")
                .column("x", "INTEGER")
                .column("y", "INTEGER")
                .column("z", "INTEGER")
                .column("id", "TEXT")
                .column("rotation", "INTEGER")
                .column("mirror", "INTEGER")
                .column("data", "TEXT")
                .primaryKey("world", "x", "y", "z")
                .execute();

            List<Row> rows = DATABASE.executor().table("multiblocks").select(rs -> {
                String world = rs.getString("world");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String id = rs.getString("id");
                int rotation = rs.getInt("rotation");
                boolean mirror = rs.getInt("mirror") != 0;
                String data = rs.getString("data");
                return new Row(world, x, y, z, id, rotation, mirror, data);
            });

            for (Row row : rows) {
                World world = Bukkit.getWorld(row.world);
                if (world == null) continue;

                Location loc = new Location(world, row.x, row.y, row.z);
                Multiblock proto = Registries.MULTIBLOCKS.get(row.id);
                if (proto == null) {
                    AbyssalLib.getInstance().getLogger().warning("Unknown multiblock id in DB: " + row.id);
                    continue;
                }

                Multiblock mb = proto.clone();
                mb.setRotation(row.rotation);
                mb.setMirrored(row.mirror);
                mb.place(loc, true);

                MultiblockEntity ent = mb.createMultiblockEntity(loc);
                if (ent != null) {
                    try {
                        ent.deserialize(JsonOps.INSTANCE, new JsonMapper().readTree(row.data));
                        ent.onLoad();
                        mb.setEntity(ent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                registerInternal(mb);

                if (loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                    ACTIVE.add(mb);
                    mb.onLoad();
                }
            }

            AbyssalLib.LOGGER.info("Loaded " + ORIGINS.size() + " Multiblocks.");
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to load multiblock database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void register(Multiblock mb) {
        Location loc = mb.getOrigin();
        if (loc == null) return;
        registerInternal(mb);
        ACTIVE.add(mb);
        save(mb);
    }

    private static void registerInternal(Multiblock mb) {
        BlockKey originKey = BlockKey.from(mb.getOrigin());
        ORIGINS.put(originKey, mb);
        mapMultiblockBlocks(mb);
    }

    public static Multiblock get(Location loc) {
        return ORIGINS.get(BlockKey.from(loc));
    }

    public static void remove(Multiblock mb) {
        Location loc = mb.getOrigin();
        if (loc == null) return;

        BlockKey originKey = BlockKey.from(loc);
        ORIGINS.remove(originKey);
        ACTIVE.remove(mb);
        unmapMultiblockBlocks(mb);

        TaskUtil.delayedAsyncTask(AbyssalLib.getInstance(), 0, () -> {
            DATABASE.executor().table("multiblocks").delete()
                .where("world = ? AND x = ? AND y = ? AND z = ?",
                    loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
                .execute();
        });
    }

    public static Multiblock getAt(Location loc) {
        return BLOCK_CACHE.get(BlockKey.from(loc));
    }

    public static boolean isPartOfMultiblock(Location loc) {
        return BLOCK_CACHE.containsKey(BlockKey.from(loc));
    }

    public static void save(Multiblock mb) {
        TaskUtil.delayedAsyncTask(AbyssalLib.getInstance(), 0, () -> {
            Location loc = mb.getOrigin();
            if (loc == null) return;

            String data = "{}";
            MultiblockEntity ent = mb.getEntity();
            if (ent != null) {
                ent.onSave();
                JsonNode node = Try.get(() -> ent.serialize(JsonOps.INSTANCE), (JsonNode) null);
                if (node == null) return;
                data = node.toString();
            }

            DATABASE.executor().table("multiblocks").replace()
                .value("world", loc.getWorld().getName())
                .value("x", loc.getBlockX())
                .value("y", loc.getBlockY())
                .value("z", loc.getBlockZ())
                .value("id", mb.getId().toString())
                .value("rotation", mb.getRotation())
                .value("mirror", mb.isMirrored() ? 1 : 0)
                .value("data", data)
                .execute();
        });
    }

    public static int save() {
        if (ORIGINS.isEmpty()) return 0;

        final int[] saved = {0};

        DATABASE.transaction(executor -> {
            for (Multiblock mb : ORIGINS.values()) {
                Location loc = mb.getOrigin();
                if (loc == null) continue;

                String data = "{}";
                MultiblockEntity ent = mb.getEntity();
                if (ent != null) {
                    ent.onSave();
                    JsonNode node = Try.get(() -> ent.serialize(JsonOps.INSTANCE), (JsonNode) null);
                    if (node == null) continue;
                    data = node.toString();
                }

                executor.table("multiblocks").replace()
                    .value("world", loc.getWorld().getName())
                    .value("x", loc.getBlockX())
                    .value("y", loc.getBlockY())
                    .value("z", loc.getBlockZ())
                    .value("id", mb.getId().toString())
                    .value("rotation", mb.getRotation())
                    .value("mirror", mb.isMirrored() ? 1 : 0)
                    .value("data", data)
                    .execute();

                saved[0]++;
            }
        });

        return saved[0];
    }

    public static List<Multiblock> getMultiblocksInChunk(Chunk chunk) {
        List<Multiblock> result = new ArrayList<>();
        String worldName = chunk.getWorld().getName();
        int cx = chunk.getX();
        int cz = chunk.getZ();

        for (Multiblock mb : ORIGINS.values()) {
            Location loc = mb.getOrigin();
            if (loc.getWorld().getName().equals(worldName) &&
                (loc.getBlockX() >> 4) == cx &&
                (loc.getBlockZ() >> 4) == cz) {
                result.add(mb);
            }
        }
        return result;
    }

    public static void loadMultiblock(Multiblock mb) {
        if (ACTIVE.add(mb)) {
            mb.onLoad();
        }
    }
    public static void unloadMultiblock(Multiblock mb) {
        if (ACTIVE.remove(mb)) {
            mb.onUnLoad();
        }
    }
    public static Collection<Multiblock> getTickingMultiblocks() {
        return ACTIVE;
    }

    private static void mapMultiblockBlocks(Multiblock mb) {
        World world = mb.getOrigin().getWorld();
        int ox = mb.getOrigin().getBlockX();
        int oy = mb.getOrigin().getBlockY();
        int oz = mb.getOrigin().getBlockZ();

        for (RelativeBlockPos rel : mb.getPattern().keySet()) {
            RelativeBlockPos rotated = Multiblock.transform(rel, mb.getRotation(), mb.isMirrored());
            BLOCK_CACHE.put(new BlockKey(world.getName(), ox + rotated.x(), oy + rotated.y(), oz + rotated.z()), mb);
        }
    }

    private static void unmapMultiblockBlocks(Multiblock mb) {
        World world = mb.getOrigin().getWorld();
        int ox = mb.getOrigin().getBlockX();
        int oy = mb.getOrigin().getBlockY();
        int oz = mb.getOrigin().getBlockZ();

        for (RelativeBlockPos rel : mb.getPattern().keySet()) {
            RelativeBlockPos rotated = Multiblock.transform(rel, mb.getRotation(), mb.isMirrored());
            BLOCK_CACHE.remove(new BlockKey(world.getName(), ox + rotated.x(), oy + rotated.y(), oz + rotated.z()));
        }
    }

    private record Row(String world, int x, int y, int z, String id, int rotation, boolean mirror, String data) {}
}