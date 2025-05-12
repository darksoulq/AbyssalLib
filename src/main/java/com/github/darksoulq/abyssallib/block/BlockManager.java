package com.github.darksoulq.abyssallib.block;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.database.Database;
import com.github.darksoulq.abyssallib.database.impl.sqlite.SqliteDatabase;
import com.github.darksoulq.abyssallib.util.FileUtils;
import com.github.darksoulq.abyssallib.util.ResourceLocation;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BlockManager {
    /**
     * The singleton instance of {@link BlockManager}.
     */
    public static final BlockManager INSTANCE = new BlockManager();
    /**
     * A cache that stores the blocks by their unique location key.
     */
    private final Map<String, Block> blockCache = new HashMap<>();

    /**
     * The database connection used to interact with the SQLite database storing block data.
     */
    private Database database;

    /**
     * Loads the block data from the SQLite database, initializing the block cache.
     */
    public void load() {
        try {
            database = new SqliteDatabase( new File(AbyssalLib.getInstance().getDataFolder(), "blocks.db"));
            database.connect();
            database.executor().table("blocks").create()
                    .ifNotExists()
                    .column("world", "TEXT")
                    .column("x", "INT")
                    .column("y", "INT")
                    .column("z", "INT")
                    .column("id", "TEXT")
                    .column("class", "TEXT")
                    .column("data", "TEXT")
                    .primaryKey("world", "x", "y", "z")
                    .execute();

            loadCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all blocks from the database into the block cache.
     *
     * @throws SQLException if a database error occurs while loading the blocks.
     */
    private void loadCache() throws SQLException {
        blockCache.clear();

        try {
            database.executor().table("blocks").select(rs -> {
                String world = rs.getString("world");
                int x = rs.getInt("x"), y = rs.getInt("y"), z = rs.getInt("z");
                String id = rs.getString("id");
                String className = rs.getString("class");

                Location loc = new Location(Bukkit.getWorld(world), x, y, z);

                try {
                    Class<?> clazz = Class.forName(className);
                    Block block = (Block) clazz.getConstructor(ResourceLocation.class)
                            .newInstance(ResourceLocation.fromString(id));
                    String dataJson = rs.getString("data");
                    BlockData data = BlockData.fromJson(FileUtils.GSON.fromJson(dataJson, JsonObject.class));
                    setSaveCallback(loc, data);
                    block.setData(data);
                    blockCache.put(key(loc), block);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null; // we don't need a return list; we're just iterating
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a callback to save block data when the data is modified.
     *
     * @param loc The location of the block.
     * @param data The block's data to associate with the save callback.
     */
    private void setSaveCallback(Location loc, BlockData data) {
        data.setSaveCallback(() -> saveBlockData(loc, data));
    }

    /**
     * Saves the block data to the SQLite database and updates the block cache.
     *
     * @param loc The location where the block is set.
     * @param block The block to set.
     */
    public void setBlockAt(Location loc, Block block) {
        setSaveCallback(loc, block.getData());
        blockCache.put(key(loc), block);

        try {
            database.executor().table("blocks").insert()
                    .value("world", loc.getWorld().getName())
                    .value("x", loc.getBlockX())
                    .value("y", loc.getBlockY())
                    .value("z", loc.getBlockZ())
                    .value("id", block.id().toString())
                    .value("class", block.getClass().getName())
                    .value("data", FileUtils.GSON.toJson(block.getData().getRaw()))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a block at a specified location from the block cache.
     *
     * @param loc The location of the block.
     * @return The block at the specified location, or {@code null} if not found.
     */
    public Block getBlockAt(Location loc) {
        return blockCache.get(key(loc));
    }

    /**
     * Removes a block at the specified location from the block cache and database.
     *
     * @param loc The location of the block to remove.
     */
    public void removeBlockAt(Location loc) {
        blockCache.remove(key(loc));
        try {
            database.executor().table("blocks").delete()
                    .where("world = ? AND x = ? AND y = ? AND z = ?",
                            loc.getWorld().getName(),
                            loc.getBlockX(),
                            loc.getBlockY(),
                            loc.getBlockZ())
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the block data for a given location in the database and updates the block cache.
     *
     * @param loc The location of the block.
     * @param data The block data to save.
     */
    public void saveBlockData(Location loc, BlockData data) {
        Block block = blockCache.get(key(loc));
        if (block == null) return;
        block.setData(data);
        blockCache.put(key(loc), block);

        try {
            database.executor().table("blocks").update()
                    .value("data", FileUtils.GSON.toJson(data.getRaw()))
                    .where("world = ? AND x = ? AND y = ? AND z = ?",
                            loc.getWorld().getName(),
                            loc.getBlockX(),
                            loc.getBlockY(),
                            loc.getBlockZ())
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a unique key for a block location based on the world and coordinates.
     *
     * @param loc The location of the block.
     * @return A unique key for the block location in the format "world;x;y;z".
     */
    private String key(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
    }
}
