package com.github.darksoulq.abyssallib.world.level.block.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.database.Database;
import com.github.darksoulq.abyssallib.server.database.impl.sqlite.SqliteDatabase;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.block.BlockEntity;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Manages custom blocks including their registration, persistence, and loading from a database.
 */
public class BlockManager {

    /**
     * Map storing blocks keyed by their location keys.
     */
    public static final Map<String, Block> BLOCKS = new HashMap<>();
    public static final List<Block> ACTIVE_BLOCKS = new ArrayList<>();
    public static final List<Block> INACTIVE_BLOCKS = new ArrayList<>();

    /**
     * List of registered Gson type adapters for custom serialization/deserialization.
     */
    private static final List<TypeAdapterRegistration<?>> ADAPTERS = new ArrayList<>();

    /**
     * Database instance used for persisting blocks.
     */
    private static Database DATABASE;

    /**
     * Gson instance configured with registered type adapters for JSON (de)serialization.
     */
    private static Gson GSON;

    /**
     * Loads blocks from the database, initializes the database if necessary, and sets up Gson.
     */
    public static void load() {
        try {
            DATABASE = new SqliteDatabase(new File(AbyssalLib.getInstance().getDataFolder(), "blocks.db"));
            DATABASE.connect();

            // Create blocks table if it doesn't exist
            DATABASE.executor().table("blocks").create()
                    .ifNotExists()
                    .column("world", "TEXT")
                    .column("x", "INTEGER")
                    .column("y", "INTEGER")
                    .column("z", "INTEGER")
                    .column("block_id", "TEXT")
                    .column("data_json", "TEXT")
                    .execute();

            buildGson();

            // Load all saved blocks
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
                    populateFieldsFromJson(entity, row.dataJson);
                    entity.onLoad();
                    block.setEntity(entity);
                }

                BLOCKS.put(locKey(loc), block);
                if (loc.isChunkLoaded()) {
                    ACTIVE_BLOCKS.add(block);
                } else {
                    INACTIVE_BLOCKS.add(block);
                }
            }
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to load block database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void buildGson() {
        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
                .setPrettyPrinting();

        for (TypeAdapterRegistration<?> reg : ADAPTERS) {
            if (reg.hierarchy) {
                builder.registerTypeHierarchyAdapter(reg.clazz, reg.adapter);
            } else {
                builder.registerTypeAdapter(reg.clazz, reg.adapter);
            }
        }

        GSON = builder.create();
    }

    /**
     * Populates the fields of the given BlockEntity from the provided JSON string.
     *
     * @param entity The block entity to populate.
     * @param json   The JSON string containing serialized field data.
     * @throws Exception If an error occurs during field reflection or JSON parsing.
     */
    private static void populateFieldsFromJson(BlockEntity entity, String json) throws Exception {
        if (json == null || json.isEmpty() || json.equals("{}")) return;

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            String name = field.getName();
            if (jsonObject.has(name)) {
                JsonElement elem = jsonObject.get(name);
                Object value = GSON.fromJson(elem, field.getType());
                field.set(entity, value);
            }
        }
    }


    /**
     * Registers or updates a block in the manager and saves it to the database.
     *
     * @param block The block to register.
     */
    public static void register(Block block) {
        Location loc = block.getLocation();
        if (loc == null) return;

        BLOCKS.put(locKey(loc), block);
        ACTIVE_BLOCKS.add(block);
        save(block);
    }

    /**
     * Retrieves the block at the specified location.
     *
     * @param loc The location to query.
     * @return The block at the location or {@code null} if none is registered.
     */
    public static Block get(Location loc) {
        return BLOCKS.get(locKey(loc));
    }

    /**
     * Removes the block at the specified location and deletes its data from the database.
     *
     * @param loc The location of the block to remove.
     */
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

    /**
     * Saves the state of a block and its associated entity to the database.
     *
     * @param block The block to save.
     */
    public static void save(Block block) {
        try {
            Location loc = block.getLocation();
            if (loc == null) return;

            BlockEntity entity = block.getEntity();
            String json;

            if (entity != null) {
                entity.onSave();

                JsonObject jsonObject = new JsonObject();
                for (Field field : entity.getClass().getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;

                    field.setAccessible(true);
                    Object value = field.get(entity);
                    JsonElement elem = GSON.toJsonTree(value);
                    jsonObject.add(field.getName(), elem);
                }

                json = GSON.toJson(jsonObject);
            } else {
                json = "{}";
            }

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


    /**
     * Saves all cached blocks and their associated entities to the database.
     * Updates existing rows or inserts new ones.
     * @return saved The amount of saved blocks
     */
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
            if (cx == chunk.getX() && cz == chunk.getZ()) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    /**
     * Creates a unique string key for a block location used internally for map keys.
     *
     * @param loc The location to convert.
     * @return A string key representing the location.
     */
    private static String locKey(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public static <T> void registerTypeAdapter(Class<T> clazz, Object adapter) {
        ADAPTERS.add(new TypeAdapterRegistration<>(clazz, adapter, false));
        buildGson();
    }

    public static <T> void registerTypeHierarchyAdapter(Class<T> clazz, Object adapter) {
        ADAPTERS.add(new TypeAdapterRegistration<>(clazz, adapter, true));
        buildGson();
    }

    private static class TypeAdapterRegistration<T> {
        final Class<T> clazz;
        final Object adapter;
        final boolean hierarchy;

        public TypeAdapterRegistration(Class<T> clazz, Object adapter, boolean hierarchy) {
            this.clazz = clazz;
            this.adapter = adapter;
            this.hierarchy = hierarchy;
        }
    }


    /**
     * Represents a row from the blocks database table.
     */
    private static class BlockRow {
        /**
         * The world name where the block is located.
         */
        final String world;

        /**
         * The X coordinate of the block location.
         */
        final int x;

        /**
         * The Y coordinate of the block location.
         */
        final int y;

        /**
         * The Z coordinate of the block location.
         */
        final int z;

        /**
         * The registered block ID.
         */
        final String blockId;

        /**
         * The serialized JSON data of the block entity.
         */
        final String dataJson;

        /**
         * Constructs a new BlockRow with the specified data.
         *
         * @param world    The world name.
         * @param x        The X coordinate.
         * @param y        The Y coordinate.
         * @param z        The Z coordinate.
         * @param blockId  The block ID.
         * @param dataJson The serialized JSON entity data.
         */
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
