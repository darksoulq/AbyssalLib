package me.darksoul.abyssalLib.block;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BlockManager {
    public static final BlockManager INSTANCE = new BlockManager();
    private final Map<String, Block> blockCache = new HashMap<>();
    private final Gson gson = new Gson();

    private Connection connection;

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
                    BlockData data = BlockData.fromJson(gson.fromJson(dataJson, JsonObject.class));
                    block.setData(data);
                    blockCache.put(key(loc), block);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void setBlockAt(Location loc, Block block) {
        blockCache.put(key(loc), block);

        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO blocks (world, x, y, z, id, class, data) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, loc.getWorld().getName());
            stmt.setInt(2, loc.getBlockX());
            stmt.setInt(3, loc.getBlockY());
            stmt.setInt(4, loc.getBlockZ());
            stmt.setString(5, block.id().toString());
            stmt.setString(6, block.getClass().getName());
            stmt.setString(7, gson.toJson(block.getData().getRaw()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Block getBlockAt(Location loc) {
        return blockCache.get(key(loc));
    }

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

    private String key(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
    }
}
