package com.github.darksoulq.abyssallib.world.block.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.relational.sql.BatchQuery;
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.github.darksoulq.abyssallib.server.util.regional.Locatable;
import com.github.darksoulq.abyssallib.server.util.regional.RegionKey;
import com.github.darksoulq.abyssallib.server.util.regional.RegionalHashMap;
import com.github.darksoulq.abyssallib.world.block.BlockEntity;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BlockManager {

    public record BlockPos(Location location) implements Locatable {
        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BlockPos(Location location1))) return false;
            return location.getWorld().getUID().equals(location1.getWorld().getUID()) &&
                location.getBlockX() == location1.getBlockX() &&
                location.getBlockY() == location1.getBlockY() &&
                location.getBlockZ() == location1.getBlockZ();
        }

        @Override
        public int hashCode() {
            return Objects.hash(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }
    }

    public static final RegionalHashMap<BlockPos, CustomBlock> BLOCKS = new RegionalHashMap<>(true);
    private static Database DATABASE;

    public static void load() {
        AbyssalLib.SCHEDULER.schedule(() -> {
            Try.run(() -> {
                DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "blocks.db"));
                DATABASE.connect();
                DATABASE.executor().create("blocks")
                    .ifNotExists()
                    .column("world", "TEXT")
                    .column("chunk_x", "INTEGER")
                    .column("chunk_z", "INTEGER")
                    .column("x", "INTEGER")
                    .column("y", "INTEGER")
                    .column("z", "INTEGER")
                    .column("block_id", "TEXT")
                    .column("data", "TEXT")
                    .primaryKey("world", "x", "y", "z")
                    .execute();

                try {
                    DATABASE.executor().executeRaw("ALTER TABLE blocks ADD COLUMN chunk_x INTEGER");
                    DATABASE.executor().executeRaw("ALTER TABLE blocks ADD COLUMN chunk_z INTEGER");
                } catch (Exception ignored) {}

                DATABASE.executor().executeRaw("UPDATE blocks SET chunk_x = CAST(FLOOR(x / 16.0) AS INTEGER), chunk_z = CAST(FLOOR(z / 16.0) AS INTEGER) WHERE chunk_x IS NULL");
                DATABASE.executor().executeRaw("CREATE INDEX IF NOT EXISTS idx_blocks_chunk ON blocks(world, chunk_x, chunk_z)");

                TextUtil.buildGson();

                AbyssalLib.SCHEDULER.schedule(() -> {
                    for (World world : Bukkit.getWorlds()) {
                        for (Chunk chunk : world.getLoadedChunks()) {
                            loadChunk(chunk);
                        }
                    }
                }).global().once();
            }).onFailure(t -> {
                AbyssalLib.getInstance().getLogger().severe("Failed to load block database: " + t.getMessage());
                t.printStackTrace();
            });
        }).async().once();

        AbyssalLib.SCHEDULER.schedule(() -> {
            int saved = save();
            if (saved > 0) {
                AbyssalLib.LOGGER.info("Saved " + saved + " blocks");
            }
        }).async().after(2400L, Clock.TICKS).repeatEvery(6000L, Clock.TICKS);
    }

    public static void shutdown() {
        int saved = save();
        if (saved > 0) {
            AbyssalLib.LOGGER.info("Saved " + saved + " blocks during shutdown.");
        }

        Try.run(() -> {
            if (DATABASE != null) {
                DATABASE.disconnect();
            }
        }).onFailure(t -> AbyssalLib.LOGGER.severe("Failed to close block database: " + t.getMessage()));
    }

    public static void loadChunk(Chunk chunk) {
        String worldName = chunk.getWorld().getName();
        int cx = chunk.getX();
        int cz = chunk.getZ();

        AbyssalLib.SCHEDULER.schedule(() -> {
            List<BlockRow> rows = DATABASE.executor().table("blocks")
                .where("world = ? AND chunk_x = ? AND chunk_z = ?", worldName, cx, cz)
                .select(rs -> new BlockRow(
                    rs.getString("world"),
                    rs.getInt("x"),
                    rs.getInt("y"),
                    rs.getInt("z"),
                    rs.getString("block_id"),
                    rs.getString("data")
                ));

            if (rows.isEmpty()) return;

            AbyssalLib.SCHEDULER.schedule(() -> {
                for (BlockRow row : rows) {
                    Location loc = new Location(chunk.getWorld(), row.x(), row.y(), row.z());
                    BlockPos pos = new BlockPos(loc);

                    if (BLOCKS.containsKey(pos)) continue;

                    if (!Registries.BLOCKS.contains(row.blockId())) {
                        AbyssalLib.getInstance().getLogger().warning("Unknown block id in DB: " + row.blockId());
                        continue;
                    }

                    CustomBlock block = Registries.BLOCKS.get(row.blockId()).clone();
                    block.setLocation(loc);

                    BlockEntity entity = block.createBlockEntity(loc);
                    if (entity != null) {
                        Try.run(() -> {
                            DataResult<Void> res = entity.deserialize(JsonOps.INSTANCE, new JsonMapper().readTree(row.dataJson()));
                            if (res.isError()) {
                                AbyssalLib.LOGGER.warning("Failed to deserialize block entity at " + loc + ": " + res.error().get());
                            } else if (res.isPartial()) {
                                res.warnings().forEach(w -> AbyssalLib.LOGGER.warning("Warning deserializing block entity at " + loc + ": " + w.message()));
                            }
                            entity.onLoad();
                            block.setEntity(entity);
                        }).onFailure(Throwable::printStackTrace);
                    }

                    block.setDirty(false);
                    block.onLoad();
                    BLOCKS.put(pos, block);
                }
            }).global().once();
        }).async().once();
    }

    public static void unloadChunk(Chunk chunk) {
        RegionKey key = new RegionKey(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
        Map<BlockPos, CustomBlock> regionMap = BLOCKS.getRegion(key);
        if (regionMap.isEmpty()) return;

        List<CustomBlock> toSave = new ArrayList<>();
        List<BlockPos> toRemove = new ArrayList<>();

        for (Map.Entry<BlockPos, CustomBlock> entry : regionMap.entrySet()) {
            CustomBlock block = entry.getValue();
            block.onUnLoad();

            if (block.isDirty()) {
                block.setDirty(false);
                toSave.add(block);
            }
            toRemove.add(entry.getKey());
        }

        for (BlockPos pos : toRemove) {
            BLOCKS.remove(pos);
        }

        if (!toSave.isEmpty()) {
            AbyssalLib.SCHEDULER.schedule(() -> {
                DATABASE.transactionResult(executor -> {
                    BatchQuery batch = executor.table("blocks")
                        .batch("world", "chunk_x", "chunk_z", "x", "y", "z", "block_id", "data")
                        .replace();
                    for (CustomBlock block : toSave) {
                        batchBlock(batch, block);
                    }
                    return batch.execute();
                });
            }).async().once();
        }
    }

    public static void unloadById(String blockId) {
        List<BlockPos> toRemove = new ArrayList<>();
        for (CustomBlock block : BLOCKS.values()) {
            if (block.getId().asString().equals(blockId)) {
                toRemove.add(new BlockPos(block.getLocation()));
            }
        }

        for (BlockPos pos : toRemove) {
            CustomBlock block = BLOCKS.remove(pos);
            if (block != null) {
                block.onUnLoad();
                if (block.isDirty()) {
                    saveAsync(block);
                }
            }
        }
    }

    public static void unloadByNamespace(String namespace) {
        List<BlockPos> toRemove = new ArrayList<>();
        for (CustomBlock block : BLOCKS.values()) {
            if (block.getId().namespace().equals(namespace)) {
                toRemove.add(new BlockPos(block.getLocation()));
            }
        }

        for (BlockPos pos : toRemove) {
            CustomBlock block = BLOCKS.remove(pos);
            if (block != null) {
                block.onUnLoad();
                if (block.isDirty()) {
                    saveAsync(block);
                }
            }
        }
    }

    public static void removeById(String blockId) {
        List<BlockPos> toRemove = new ArrayList<>();
        for (CustomBlock block : BLOCKS.values()) {
            if (block.getId().asString().equals(blockId)) {
                toRemove.add(new BlockPos(block.getLocation()));
            }
        }

        for (BlockPos pos : toRemove) {
            CustomBlock block = BLOCKS.remove(pos);
            if (block != null) block.onUnLoad();
        }

        AbyssalLib.SCHEDULER.schedule(() -> {
            DATABASE.executor().table("blocks").delete()
                .where("block_id = ?", blockId)
                .execute();
        }).async().once();
    }

    public static void removeByNamespace(String namespace) {
        List<BlockPos> toRemove = new ArrayList<>();
        for (CustomBlock block : BLOCKS.values()) {
            if (block.getId().namespace().equals(namespace)) {
                toRemove.add(new BlockPos(block.getLocation()));
            }
        }

        for (BlockPos pos : toRemove) {
            CustomBlock block = BLOCKS.remove(pos);
            if (block != null) block.onUnLoad();
        }

        AbyssalLib.SCHEDULER.schedule(() -> {
            DATABASE.executor().table("blocks").delete()
                .where("block_id LIKE ?", namespace + ":%")
                .execute();
        }).async().once();
    }

    public static void reindexChunks() {
        AbyssalLib.SCHEDULER.schedule(() -> {
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    loadChunk(chunk);
                }
            }
        }).global().once();
    }

    private static void saveAsync(CustomBlock block) {
        AbyssalLib.SCHEDULER.schedule(() -> {
            DATABASE.transactionResult(executor -> {
                BatchQuery batch = executor.table("blocks")
                    .batch("world", "chunk_x", "chunk_z", "x", "y", "z", "block_id", "data")
                    .replace();
                batchBlock(batch, block);
                return batch.execute();
            });
        }).async().once();
    }

    public static void register(CustomBlock block) {
        Location loc = block.getLocation();
        if (loc == null) return;

        block.markDirty();
        BLOCKS.put(new BlockPos(loc), block);
    }

    public static CustomBlock get(Location loc) {
        if (loc == null) return null;
        return BLOCKS.get(new BlockPos(loc));
    }

    public static void remove(CustomBlock block) {
        remove(block.getLocation());
    }

    public static void remove(Location loc) {
        if (loc == null) return;

        CustomBlock block = BLOCKS.remove(new BlockPos(loc));
        if (block != null) {
            block.onUnLoad();
        }

        AbyssalLib.SCHEDULER.schedule(() -> {
            DATABASE.executor().table("blocks").delete()
                .where("world = ? AND x = ? AND y = ? AND z = ?",
                    loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
                .execute();
        }).async().once();
    }

    public static void save(CustomBlock block) {
        block.markDirty();
    }

    public static int save() {
        if (BLOCKS.isEmpty()) return 0;

        List<CustomBlock> toSave = new ArrayList<>();
        for (CustomBlock block : BLOCKS.values()) {
            if (block.isDirty()) {
                block.setDirty(false);
                toSave.add(block);
            }
        }

        if (toSave.isEmpty()) return 0;

        return DATABASE.transactionResult(executor -> {
            BatchQuery batch = executor.table("blocks")
                .batch("world", "chunk_x", "chunk_z", "x", "y", "z", "block_id", "data")
                .replace();

            for (CustomBlock block : toSave) {
                batchBlock(batch, block);
            }

            return batch.execute();
        });
    }

    private static void batchBlock(BatchQuery batch, CustomBlock block) {
        Location loc = block.getLocation();
        if (loc == null) return;

        BlockEntity entity = block.getEntity();
        String json = "{}";

        if (entity != null) {
            entity.onSave();
            JsonNode node = Try.of(() -> {
                DataResult<JsonNode> res = entity.serialize(JsonOps.INSTANCE);
                if (res.isError()) {
                    AbyssalLib.LOGGER.warning("Failed to serialize block entity at " + loc + ": " + res.error().get());
                    return null;
                }
                if (res.isPartial()) {
                    res.warnings().forEach(w -> AbyssalLib.LOGGER.warning("Warning serializing block entity at " + loc + ": " + w.message()));
                }
                return res.getOrThrow();
            }).orElse(null);

            if (node != null) {
                json = node.toString();
            }
        }

        batch.add(
            loc.getWorld().getName(),
            loc.getBlockX() >> 4,
            loc.getBlockZ() >> 4,
            loc.getBlockX(),
            loc.getBlockY(),
            loc.getBlockZ(),
            block.getId().toString(),
            json
        );
    }

    public static List<CustomBlock> getBlocksInChunk(Chunk chunk) {
        RegionKey key = new RegionKey(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
        return new ArrayList<>(BLOCKS.getRegion(key).values());
    }

    private record BlockRow(String world, int x, int y, int z, String blockId, String dataJson) {
    }
}