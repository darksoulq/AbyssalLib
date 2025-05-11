package me.darksoul.abyssallib.block;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.darksoul.abyssallib.AbyssalLib;
import me.darksoul.abyssallib.util.FileUtils;
import me.darksoul.abyssallib.util.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.sql.*;
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
    private Connection connection;

    /**
     * Loads the block data from the SQLite database, initializing the block cache.
     */
    public void load() {
        try {
            File dbFile = new File(AbyssalLib.getInstance().getDataFolder(), "blocks.db");
            dbFile.getParentFile().mkdirs();
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS blocks (
                    world TEXT,
                    x INT,
                    y INT,
                    z INT,
                    id TEXT,
                    class TEXT,
                    data TEXT,
                    PRIMARY KEY(world, x, y, z)
                )
            """);

            loadCache();
        } catch (SQLException e) {
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
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM blocks")) {
            while (rs.next()) {
                String world = rs.getString("world");
                int x = rs.getInt("x"), y = rs.getInt("y"), z = rs.getInt("z");
                String id = rs.getString("id");
                String className = rs.getString("class");

                Location loc = new Location(Bukkit.getWorld(world), x, y, z);

                try {
                    Class<?> clazz = Class.forName(className);
                    Block block = (Block) clazz.getConstructor(ResourceLocation.class).newInstance(ResourceLocation.fromString(id));
                    String dataJson = rs.getString("data");
                    BlockData data = BlockData.fromJson(FileUtils.GSON.fromJson(dataJson, JsonObject.class));
                    setSaveCallback(loc, data);
                    block.setData(data);
                    blockCache.put(key(loc), block);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
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

        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO blocks (world, x, y, z, id, class, data) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, loc.getWorld().getName());
            stmt.setInt(2, loc.getBlockX());
            stmt.setInt(3, loc.getBlockY());
            stmt.setInt(4, loc.getBlockZ());
            stmt.setString(5, block.id().toString());
            stmt.setString(6, block.getClass().getName());
            stmt.setString(7, FileUtils.GSON.toJson(block.getData().getRaw()));
            stmt.executeUpdate();
        } catch (SQLException e) {
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
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM blocks WHERE world = ? AND x = ? AND y = ? AND z = ?")) {
            stmt.setString(1, loc.getWorld().getName());
            stmt.setInt(2, loc.getBlockX());
            stmt.setInt(3, loc.getBlockY());
            stmt.setInt(4, loc.getBlockZ());
            stmt.executeUpdate();
        } catch (SQLException e) {
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

        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE blocks SET data = ? WHERE world = ? AND x = ? AND y = ? AND z = ?")) {
            stmt.setString(1, FileUtils.GSON.toJson(data.getRaw()));
            stmt.setString(2, loc.getWorld().getName());
            stmt.setInt(3, loc.getBlockX());
            stmt.setInt(4, loc.getBlockY());
            stmt.setInt(5, loc.getBlockZ());
            stmt.executeUpdate();
        } catch (SQLException e) {
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
