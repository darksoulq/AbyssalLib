package com.github.darksoulq.abyssallib.world.block.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.sql.BatchQuery;
import com.github.darksoulq.abyssallib.common.database.sql.Database;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.util.TaskUtil;
import com.github.darksoulq.abyssallib.world.block.BlockEntity;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockManager {

    public static final Map<String, CustomBlock> BLOCKS = new ConcurrentHashMap<>();
    public static final List<Location> ACTIVE_BLOCKS = new CopyOnWriteArrayList<>();
    private static Database DATABASE;

    public static void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int saved = BlockManager.save();
                if (saved > 0) {
                    AbyssalLib.LOGGER.info("Saved " + saved + " blocks");
                }
            }
        }.runTaskTimerAsynchronously(AbyssalLib.getInstance(), 20L * 60 * 2, 20L * 60 * 5);

        Try.run(() -> {
            DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "blocks.db"));
            DATABASE.connect();
            DATABASE.executor().create("blocks")
                .ifNotExists()
                .column("world", "TEXT")
                .column("x", "INTEGER")
                .column("y", "INTEGER")
                .column("z", "INTEGER")
                .column("block_id", "TEXT")
                .column("data", "TEXT")
                .primaryKey("world", "x", "y", "z")
                .execute();

            TextUtil.buildGson();

            List<BlockRow> rows = DATABASE.executor().table("blocks").select(rs -> {
                String world = rs.getString("world");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String blockId = rs.getString("block_id");
                String dataJson = rs.getString("data");
                return new BlockRow(world, x, y, z, blockId, dataJson);
            });

            for (BlockRow row : rows) {
                if (Bukkit.getWorld(row.world) == null) continue;

                Location loc = new Location(Bukkit.getWorld(row.world), row.x, row.y, row.z);

                if (!Registries.BLOCKS.contains(row.blockId)) {
                    AbyssalLib.getInstance().getLogger().warning("Unknown block id in DB: " + row.blockId);
                    continue;
                }

                CustomBlock block = Registries.BLOCKS.get(row.blockId).clone();
                block.setLocation(loc);

                BlockEntity entity = block.createBlockEntity(loc);
                if (entity != null) {
                    Try.run(() -> {
                        entity.deserialize(JsonOps.INSTANCE, new JsonMapper().readTree(row.dataJson));
                        entity.onLoad();
                        block.setEntity(entity);
                    }).onFailure(Throwable::printStackTrace);
                }

                block.onLoad();

                BLOCKS.put(locKey(loc), block);
            }

            AbyssalLib.LOGGER.info("Loaded " + BLOCKS.size() + " Blocks.");
        }).onFailure(t -> {
            AbyssalLib.getInstance().getLogger().severe("Failed to load block database: " + t.getMessage());
            t.printStackTrace();
        });
    }

    public static void register(CustomBlock block) {
        Location loc = block.getLocation();
        if (loc == null) return;

        BLOCKS.put(locKey(loc), block);
        ACTIVE_BLOCKS.add(block.getLocation());
        save(block);
    }

    public static CustomBlock get(Location loc) {
        return BLOCKS.get(locKey(loc));
    }

    public static void remove(CustomBlock block) {
        remove(block.getLocation());
    }

    public static void remove(Location loc) {
        BLOCKS.remove(locKey(loc));
        ACTIVE_BLOCKS.remove(loc);

        TaskUtil.delayedAsyncTask(AbyssalLib.getInstance(), 0, () -> {
            DATABASE.executor().table("blocks").delete()
                .where("world = ? AND x = ? AND y = ? AND z = ?",
                    loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
                .execute();
        });
    }

    public static void save(CustomBlock block) {
        TaskUtil.delayedAsyncTask(AbyssalLib.getInstance(), 0, () -> {
            Location loc = block.getLocation();
            if (loc == null) return;

            BlockEntity entity = block.getEntity();
            String json;

            if (entity != null) {
                entity.onSave();
                JsonNode node = Try.of(() -> entity.serialize(JsonOps.INSTANCE)).orElse(null);
                if (node == null) return;
                json = node.toString();
            } else {
                json = "{}";
            }
            DATABASE.executor().table("blocks").replace()
                .value("world", loc.getWorld().getName())
                .value("x", loc.getBlockX())
                .value("y", loc.getBlockY())
                .value("z", loc.getBlockZ())
                .value("block_id", block.getId().toString())
                .value("data", json)
                .execute();
        });
    }

    public static int save() {
        if (BLOCKS.isEmpty()) return 0;
        return DATABASE.transactionResult(executor -> {
            BatchQuery batch = executor.table("blocks")
                .batch("world", "x", "y", "z", "block_id", "data")
                .replace();

            int count = 0;

            for (CustomBlock block : BLOCKS.values()) {
                Location loc = block.getLocation();
                if (loc == null) continue;

                BlockEntity entity = block.getEntity();
                String json;

                if (entity != null) {
                    entity.onSave();
                    JsonNode node = Try.of(() -> entity.serialize(JsonOps.INSTANCE)).orElse(null);
                    if (node == null) continue;
                    json = node.toString();
                } else {
                    json = "{}";
                }
                batch.add(
                    loc.getWorld().getName(),
                    loc.getBlockX(),
                    loc.getBlockY(),
                    loc.getBlockZ(),
                    block.getId().toString(),
                    json
                );
                count++;
            }

            if (count > 0) {
                batch.execute();
            }
            return count;
        });
    }

    public static List<CustomBlock> getBlocksInChunk(Chunk chunk) {
        List<CustomBlock> blocks = new ArrayList<>();
        String worldName = chunk.getWorld().getName();

        for (CustomBlock block : BLOCKS.values()) {
            Location loc = block.getLocation();
            if (!loc.getWorld().getName().equals(worldName)) continue;
            int cx = loc.getBlockX() >> 4;
            int cz = loc.getBlockZ() >> 4;
            if (cx == chunk.getX() && cz == chunk.getZ()) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    private static String locKey(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private record BlockRow(String world, int x, int y, int z, String blockId, String dataJson) { }
}