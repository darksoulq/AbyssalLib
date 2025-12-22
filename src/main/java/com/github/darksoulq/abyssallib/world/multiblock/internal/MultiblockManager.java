package com.github.darksoulq.abyssallib.world.multiblock.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.Database;
import com.github.darksoulq.abyssallib.common.database.impl.sqlite.SqliteDatabase;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiblockManager {
    private static Database DATABASE;

    public static final Map<String, Multiblock> MULTIBLOCKS = new HashMap<>();
    public static final List<Location> ACTIVE_MULTIBLOCKS = new ArrayList<>();

    public static void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int saved = MultiblockManager.save();
                AbyssalLib.LOGGER.info("Saved " + saved + " multiblocks");
            }
        }.runTaskTimerAsynchronously(AbyssalLib.getInstance(), 20L * 60 * 2, 20L * 60 * 5);

        try {
            DATABASE = new SqliteDatabase(new File(AbyssalLib.getInstance().getDataFolder(), "multiblocks.db"));
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
                return new Row(world, x, y, z, id, rotation, data);
            });

            for (Row row : rows) {
                Location loc = new Location(Bukkit.getWorld(row.world), row.x, row.y, row.z);
                Multiblock proto = Registries.MULTIBLOCKS.get(row.id);
                if (proto == null) {
                    AbyssalLib.getInstance().getLogger().warning("Unknown multiblock id in DB: " + row.id);
                    continue;
                }
                Multiblock mb = proto.clone();
                mb.setRotation(row.rotation);
                mb.place(loc, true);
                MultiblockEntity ent = mb.createMultiblockEntity(loc);
                if (ent != null) {
                    ent.deserialize(JsonOps.INSTANCE, new JsonMapper().readTree(row.data));
                    ent.onLoad();
                    mb.setEntity(ent);
                }
                MULTIBLOCKS.put(locKey(loc), mb);
                ACTIVE_MULTIBLOCKS.add(loc);
            }

            AbyssalLib.LOGGER.info("Loaded " + MULTIBLOCKS.size() + " Multiblocks.");
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to load multiblock database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void register(Multiblock mb) {
        Location loc = mb.getOrigin();
        if (loc == null) return;
        MULTIBLOCKS.put(locKey(loc), mb);
        ACTIVE_MULTIBLOCKS.add(loc);
        save(mb);
    }

    public static Multiblock get(Location loc) {
        return MULTIBLOCKS.get(locKey(loc));
    }

    public static void remove(Multiblock mb) {
        Location loc = mb.getOrigin();
        if (loc == null) return;
        for (RelativeBlockPos rel : mb.getPattern().keySet()) {
            Location abs = Multiblock.absolute(mb.getOrigin(), Multiblock.transform(rel, mb.getRotation(), mb.isMirrored()));
            MULTIBLOCKS.remove(locKey(abs));
        }
        ACTIVE_MULTIBLOCKS.remove(loc);
        TaskUtil.delayedAsyncTask(AbyssalLib.getInstance(), 0, () -> {
            DATABASE.executor().table("multiblocks").delete()
                .where("world = ?", loc.getWorld().getName())
                .where("x = ?", loc.getBlockX())
                .where("y = ?", loc.getBlockY())
                .where("z = ?", loc.getBlockZ())
                .execute();
        });
    }

    public static Multiblock getAt(Location loc) {
        for (Multiblock mb : MULTIBLOCKS.values()) {
            if (mb.isPartOfMultiblock(loc)) return mb;
        }
        return null;
    }

    public static boolean isPartOfMultiblock(Location loc) {
        return getAt(loc) != null;
    }

    public static void save(Multiblock mb) {
        Location loc = mb.getOrigin();
        TaskUtil.delayedAsyncTask(AbyssalLib.getInstance(), 0, () -> {
            String data = "{}";
            MultiblockEntity ent = mb.getEntity();
            if (ent != null) {
                ent.onSave();
                JsonNode node = Try.get(() -> ent.serialize(JsonOps.INSTANCE), (JsonNode) null);
                if (node == null) return;
                data = node.toString();
            }
            DATABASE.executor().table("multiblocks").insert()
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
        int saved = 0;
        for (Multiblock mb : MULTIBLOCKS.values()) {
            save(mb);
            saved++;
        }
        return saved;
    }

    public static List<Multiblock> getMultiblocksInChunk(Chunk chunk) {
        List<Multiblock> result = new ArrayList<>();
        String worldName = chunk.getWorld().getName();
        for (Multiblock mb : MULTIBLOCKS.values()) {
            Location loc = mb.getOrigin();
            if (!loc.getWorld().getName().equals(worldName)) continue;
            int cx = loc.getBlockX() >> 4;
            int cz = loc.getBlockZ() >> 4;
            if (cx == chunk.getX() && cz == chunk.getZ()) result.add(mb);
        }
        return result;
    }

    private static String locKey(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private record Row(String world, int x, int y, int z, String id, int rotation, String data) {}
}
