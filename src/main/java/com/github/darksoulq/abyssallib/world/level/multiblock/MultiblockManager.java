package com.github.darksoulq.abyssallib.world.level.multiblock;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.database.Database;
import com.github.darksoulq.abyssallib.server.database.impl.sqlite.SqliteDatabase;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class MultiblockManager {

    private static final Map<Location, MultiblockInstance> INSTANCE_ORIGINS = new HashMap<>();
    private static final Map<Location, MultiblockInstance> INSTANCE_BLOCKS = new HashMap<>();
    private static Database DB;

    public static void load() throws Exception {
        if (TextUtil.GSON == null) {
            TextUtil.buildGson();
        }
        DB = new SqliteDatabase(new File(AbyssalLib.getInstance().getDataFolder(), "multiblocks.db"));
        DB.connect();
        DB.executor().table("multiblocks").create().ifNotExists()
                .column("world", "TEXT")
                .column("x", "INTEGER")
                .column("y", "INTEGER")
                .column("z", "INTEGER")
                .column("id", "TEXT")
                .column("data", "TEXT")
                .execute();

        var rows = DB.executor().table("multiblocks").select(rs ->
                new Row(rs.getString("world"),
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getInt("z"),
                        rs.getString("id"), 
                        rs.getString("data")
                ));
        for (Row r : rows) {
            World w = Bukkit.getWorld(r.world);
            if (w == null) continue;
            Location origin = new Location(w, r.x, r.y, r.z);
            Multiblock mb = Registries.MULTIBLOCKS.get(r.id);
            if (mb == null) continue;
            MultiblockInstance inst = new MultiblockInstance(origin, mb);
            if (r.data != null && !r.data.isEmpty()) {
                inst.setData(inst.getType().deserializeData(r.data));
            }
            register(inst);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (MultiblockInstance ins : MultiblockManager.getAll()) {
                    ins.tickIfApplicable();
                }
            }
        }.runTaskTimer(AbyssalLib.getInstance(), 0, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (MultiblockInstance ins : MultiblockManager.getAll()) {
                    ins.tickIfApplicable();
                }
            }
        }.runTaskTimer(AbyssalLib.getInstance(), 0, 20 * 60 * 5);
    }

    public static void register(MultiblockInstance inst) {
        Location origin = normalize(inst.getOrigin());
        INSTANCE_ORIGINS.put(origin, inst);

        for (RelativeBlockPos rel : inst.getType().getPattern().keySet()) {
            Location abs = origin.clone().add(rel.x(), rel.y(), rel.z());
            INSTANCE_BLOCKS.put(normalize(abs), inst);
        }

        save(inst);
    }

    public static void remove(MultiblockInstance inst) {
        Location origin = normalize(inst.getOrigin());
        INSTANCE_ORIGINS.remove(origin);

        for (RelativeBlockPos rel : inst.getType().getPattern().keySet()) {
            Location abs = origin.clone().add(rel.x(), rel.y(), rel.z());
            INSTANCE_BLOCKS.remove(normalize(abs));
        }

        try {
            DB.executor().table("multiblocks").delete()
                    .where("world", origin.getWorld().getName())
                    .where("x", origin.getBlockX())
                    .where("y", origin.getBlockY())
                    .where("z", origin.getBlockZ())
                    .execute();
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().warning("Failed removing multiblock");
        }
    }

    public static Collection<MultiblockInstance> getAll() {
        return Collections.unmodifiableCollection(INSTANCE_ORIGINS.values());
    }

    public static MultiblockInstance getAt(Location loc) {
        return INSTANCE_BLOCKS.get(normalize(loc));
    }

    public static void save(MultiblockInstance inst) {
        Location o = normalize(inst.getOrigin());
        try {
            DB.executor().table("multiblocks").insert()
                    .value("world", o.getWorld().getName())
                    .value("x", o.getBlockX())
                    .value("y", o.getBlockY())
                    .value("z", o.getBlockZ())
                    .value("id", inst.getType().getId().toString())
                    .value("data", TextUtil.GSON.toJson(inst.getData()))
                    .execute();
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed saving multiblock");
        }
    }

    public static Location normalize(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    private record Row(String world, int x, int y, int z, String id, String data) {}
}
