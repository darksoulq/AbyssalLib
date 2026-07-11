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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
            int saved = BlockManager.save();
            if (saved > 0) {
                AbyssalLib.LOGGER.info("Saved " + saved + " blocks");
            }
        }).async().after(2400L, Clock.TICKS).repeatEvery(6000L, Clock.TICKS);

        AbyssalLib.SCHEDULER.schedule(() -> {
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

                AbyssalLib.SCHEDULER.schedule(() -> {
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
                                DataResult<Void> res = entity.deserialize(JsonOps.INSTANCE, new JsonMapper().readTree(row.dataJson));
                                if (res.isError()) {
                                    AbyssalLib.LOGGER.warning("Failed to deserialize block entity at " + loc + ": " + res.error().get());
                                } else if (res.isPartial()) {
                                    res.warnings().forEach(w -> AbyssalLib.LOGGER.warning("Warning deserializing block entity at " + loc + ": " + w.message()));
                                }
                                entity.onLoad();
                                block.setEntity(entity);
                            }).onFailure(Throwable::printStackTrace);
                        }

                        block.onLoad();
                        BLOCKS.put(new BlockPos(loc), block);
                    }

                    AbyssalLib.LOGGER.info("Loaded " + BLOCKS.size() + " Blocks.");
                }).global().once();
            }).onFailure(t -> {
                AbyssalLib.getInstance().getLogger().severe("Failed to load block database: " + t.getMessage());
                t.printStackTrace();
            });
        }).async().once();
    }

    public static void register(CustomBlock block) {
        Location loc = block.getLocation();
        if (loc == null) return;

        BLOCKS.put(new BlockPos(loc), block);
        save(block);
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
        AbyssalLib.SCHEDULER.schedule(() -> {
            Location loc = block.getLocation();
            if (loc == null) return;

            BlockEntity entity = block.getEntity();
            String json;

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
        }).async().once();
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
        RegionKey key = new RegionKey(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
        return new ArrayList<>(BLOCKS.getRegion(key).values());
    }

    private record BlockRow(String world, int x, int y, int z, String blockId, String dataJson) {
    }
}