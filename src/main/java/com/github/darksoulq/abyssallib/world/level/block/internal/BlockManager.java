package com.github.darksoulq.abyssallib.world.level.block.internal;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.config.serializer.Serializer;
import com.github.darksoulq.abyssallib.server.config.serializer.SerializerRegistry;
import com.github.darksoulq.abyssallib.server.database.Database;
import com.github.darksoulq.abyssallib.server.database.impl.sqlite.SqliteDatabase;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.util.TextUtil;
import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.block.BlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class BlockManager {
    public static final Map<String, Block> BLOCKS = new HashMap<>();
    public static final List<Block> ACTIVE_BLOCKS = new ArrayList<>();
    public static final List<Block> INACTIVE_BLOCKS = new ArrayList<>();
    private static Database DATABASE;

    public static void load() {
        try {
            DATABASE = new SqliteDatabase(new File(AbyssalLib.getInstance().getDataFolder(), "blocks.db"));
            DATABASE.connect();
            DATABASE.executor().table("blocks").create()
                    .ifNotExists()
                    .column("world", "TEXT")
                    .column("x", "INTEGER")
                    .column("y", "INTEGER")
                    .column("z", "INTEGER")
                    .column("block_id", "TEXT")
                    .column("data_json", "TEXT")
                    .execute();

            List<BlockRow> rows = DATABASE.executor().table("blocks").select(rs -> {
                String world = rs.getString("world");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String blockId = rs.getString("block_id");
                String dataJson = rs.getString("data_json");
                return new BlockRow(world, x, y, z, blockId, dataJson);
            });

            for (BlockRow row : rows) {
                Location loc = new Location(Bukkit.getWorld(row.world), row.x, row.y, row.z);
                Block block = Registries.BLOCKS.get(row.blockId).clone();
                if (block == null) {
                    AbyssalLib.getInstance().getLogger().warning("Unknown block id in DB: " + row.blockId);
                    continue;
                }
                block.place(loc.getBlock(), true);
                BlockEntity entity = block.createBlockEntity(loc);
                if (entity != null) {
                    populateFieldsFromConfig(entity, row.dataJson);
                    entity.onLoad();
                    block.setEntity(entity);
                }
                BLOCKS.put(locKey(loc), block);
                if (loc.isChunkLoaded()) ACTIVE_BLOCKS.add(block);
                else INACTIVE_BLOCKS.add(block);
            }
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to load block database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void populateFieldsFromConfig(BlockEntity entity, String json) throws Exception {
        if (json == null || json.isEmpty() || json.equals("{}")) return;
        Map<String, Object> map = TextUtil.GSON.fromJson(json, Map.class);
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            if (map.containsKey(field.getName()) && SerializerRegistry.has(field.getType())) {
                Object value = SerializerRegistry.get(field.getType()).deserialize(map.get(field.getName()));
                field.set(entity, value);
            }
        }
    }

    public static void register(Block block) {
        Location loc = block.getLocation();
        if (loc == null) return;
        BLOCKS.put(locKey(loc), block);
        ACTIVE_BLOCKS.add(block);
        save(block);
    }

    public static Block get(Location loc) {
        return BLOCKS.get(locKey(loc));
    }

    public static void remove(Location loc) {
        BLOCKS.remove(locKey(loc));
        try {
            DATABASE.executor().table("blocks").delete()
                    .where("world", loc.getWorld().getName())
                    .where("x", loc.getBlockX())
                    .where("y", loc.getBlockY())
                    .where("z", loc.getBlockZ())
                    .execute();
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().warning("Failed to remove block: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void save(Block block) {
        try {
            Location loc = block.getLocation();
            if (loc == null) return;
            BlockEntity entity = block.getEntity();
            Map<String, Object> dataMap = new LinkedHashMap<>();
            if (entity != null) {
                entity.onSave();
                for (Field field : entity.getClass().getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (SerializerRegistry.has(field.getType())) {
                        value = ((Serializer<Object>) SerializerRegistry.get(field.getType())).serialize(value);
                    }
                    dataMap.put(field.getName(), value);
                }
            }
            String json = TextUtil.GSON.toJson(dataMap);
            DATABASE.executor().table("blocks").insert()
                    .value("world", loc.getWorld().getName())
                    .value("x", loc.getBlockX())
                    .value("y", loc.getBlockY())
                    .value("z", loc.getBlockZ())
                    .value("block_id", block.getId().toString())
                    .value("data_json", json)
                    .execute();
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().warning("Failed to save block: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int save() {
        int saved = 0;
        for (Block block : BLOCKS.values()) {
            save(block);
            saved++;
        }
        return saved;
    }

    public static List<Block> getBlocksInChunk(Chunk chunk) {
        List<Block> blocks = new ArrayList<>();
        String worldName = chunk.getWorld().getName();
        for (Block block : BLOCKS.values()) {
            Location loc = block.getLocation();
            if (!loc.getWorld().getName().equals(worldName)) continue;
            int cx = loc.getBlockX() >> 4;
            int cz = loc.getBlockZ() >> 4;
            if (cx == chunk.getX() && cz == chunk.getZ()) blocks.add(block);
        }
        return blocks;
    }

    private static String locKey(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private static class BlockRow {
        final String world;
        final int x, y, z;
        final String blockId;
        final String dataJson;

        public BlockRow(String world, int x, int y, int z, String blockId, String dataJson) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockId = blockId;
            this.dataJson = dataJson;
        }
    }
}
