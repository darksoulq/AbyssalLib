package com.github.darksoulq.abyssallib.world.block.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
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

public class BlockManager {

    public static final Map<String, CustomBlock> BLOCKS = new HashMap<>();
    public static final List<Location> ACTIVE_BLOCKS = new ArrayList<>();
    private static Database DATABASE;

    public static void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int saved = BlockManager.save();
                AbyssalLib.LOGGER.info("Saved " + saved + " blocks");
            }
        }.runTaskTimerAsynchronously(AbyssalLib.getInstance(), 20L * 60 * 2, 20L * 60 * 5);

        try {
            DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "blocks.db"));
            DATABASE.connect();

            DATABASE.executor().table("blocks").create()
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
                block.place(loc.getBlock(), true);

                BlockEntity entity = block.createBlockEntity(loc);
                if (entity != null) {
                    try {
                        entity.deserialize(JsonOps.INSTANCE, new JsonMapper().readTree(row.dataJson));
                        entity.onLoad();
                        block.setEntity(entity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                BLOCKS.put(locKey(loc), block);
            }

            AbyssalLib.LOGGER.info("Loaded " + BLOCKS.size() + " Blocks.");
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to load block database: " + e.getMessage());
            e.printStackTrace();
        }
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

    public static void remove(Location loc, CustomBlock block) {
        BLOCKS.remove(locKey(loc));
        ACTIVE_BLOCKS.remove(block.getLocation());
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
                JsonNode node = Try.get(() -> entity.serialize(JsonOps.INSTANCE), (JsonNode) null);
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

        final int[] saved = {0};
        DATABASE.transaction(executor -> {
            for (CustomBlock block : BLOCKS.values()) {
                Location loc = block.getLocation();
                if (loc == null) continue;

                BlockEntity entity = block.getEntity();
                String json;

                if (entity != null) {
                    entity.onSave();
                    JsonNode node = Try.get(() -> entity.serialize(JsonOps.INSTANCE), (JsonNode) null);
                    if (node == null) continue;
                    json = node.toString();
                } else {
                    json = "{}";
                }

                executor.table("blocks").replace()
                    .value("world", loc.getWorld().getName())
                    .value("x", loc.getBlockX())
                    .value("y", loc.getBlockY())
                    .value("z", loc.getBlockZ())
                    .value("block_id", block.getId().toString())
                    .value("data", json)
                    .execute();

                saved[0]++;
            }
        });

        return saved[0];
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