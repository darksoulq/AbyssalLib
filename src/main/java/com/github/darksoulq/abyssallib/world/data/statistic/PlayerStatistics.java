package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.sql.BatchQuery;
import com.github.darksoulq.abyssallib.common.database.sql.Database;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ops.StringOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles statistics for individual players, including loading, saving,
 * and caching of player statistics.
 */
public class PlayerStatistics {
    private static final Map<UUID, PlayerStatistics> CACHE = new ConcurrentHashMap<>();
    private static final Database DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "player_statistics.db"));

    private final UUID uuid;
    private final Map<Identifier, Statistic> stats = new ConcurrentHashMap<>();

    private PlayerStatistics(UUID uuid) {
        this.uuid = uuid;
        load();
    }

    public static void init() {
        try {
            DATABASE.connect();
            initTable();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize PlayerStatistics database", e);
        }
    }

    /**
     * Retrieves the PlayerStatistics object for a given player.
     *
     * @param player the player
     * @return the PlayerStatistics instance
     */
    public static PlayerStatistics of(Player player) {
        return of(player.getUniqueId());
    }

    /**
     * Retrieves the PlayerStatistics object for a given UUID.
     * If not already cached, a new instance is created and cached.
     *
     * @param uuid the player's UUID
     * @return the PlayerStatistics instance
     */
    public static PlayerStatistics of(UUID uuid) {
        return CACHE.computeIfAbsent(uuid, PlayerStatistics::new);
    }

    /**
     * Gets a statistic by its identifier.
     * Returns a clone to prevent direct modification of the internal state.
     *
     * @param id the identifier of the statistic
     * @return a cloned Statistic, or null if not found
     */
    public Statistic get(Identifier id) {
        Statistic stat = stats.get(id);
        return stat != null ? stat.clone() : null;
    }

    public void set(Statistic stat) {
        stats.put(stat.getId(), stat.clone());
        save(stat);
    }

    public List<Statistic> get() {
        return stats.values().stream().toList();
    }

    private void save(Statistic stat) {
        DATABASE.executor().table("player_statistics").replace()
            .value("uuid", uuid.toString())
            .value("key", stat.getId().toString())
            .value("value", stat.getValue().toString())
            .executeAsync()
            .exceptionally(ex -> {
                AbyssalLib.getInstance().getLogger().warning("Failed to save statistic " + stat.getId() + ": " + ex.getMessage());
                return null;
            });
    }

    private void load() {
        DATABASE.executor().table("player_statistics")
            .where("uuid = ?", uuid.toString())
            .selectAsync(rs -> Map.entry(rs.getString("key"), rs.getString("value")))
            .thenAccept(rows -> {
                Map<Identifier, Statistic> tempStats = new ConcurrentHashMap<>();
                List<Statistic> newDefaultsToSave = new ArrayList<>();
                for (var entry : rows) {
                    Identifier id = Identifier.of(entry.getKey());

                    if (!Registries.STATISTICS.contains(id.toString())) {
                        AbyssalLib.getInstance().getLogger().warning("Statistic not registered: " + id);
                        continue;
                    }

                    Statistic template = Registries.STATISTICS.get(id.toString()).clone();
                    Object value = Try.get(() -> Codec.oneOf(Codecs.INT, Codecs.FLOAT, Codecs.BOOLEAN)
                        .decode(StringOps.INSTANCE, entry.getValue()), template.getValue());

                    template.setValue(value);
                    tempStats.put(id, template);
                }

                Registries.STATISTICS.getAll().forEach((key, template) -> {
                    Identifier id = Identifier.of(key);
                    if (!tempStats.containsKey(id)) {
                        Statistic clone = template.clone();
                        tempStats.put(id, clone);
                        newDefaultsToSave.add(clone);
                    }
                });
                this.stats.putAll(tempStats);

                if (!newDefaultsToSave.isEmpty()) {
                    saveDefaultsBatch(newDefaultsToSave);
                }
            })
            .exceptionally(ex -> {
                AbyssalLib.getInstance().getLogger().severe("Failed to load statistics for " + uuid + ": " + ex.getMessage());
                return null;
            });
    }

    private void saveDefaultsBatch(List<Statistic> newStats) {
        if (newStats.isEmpty()) return;

        BatchQuery batch = DATABASE.executor().table("player_statistics")
            .batch("uuid", "key", "value")
            .insertIgnore();

        for (Statistic stat : newStats) {
            batch.add(uuid.toString(), stat.getId().toString(), stat.getValue().toString());
        }

        batch.executeAsync().exceptionally(ex -> {
            AbyssalLib.getInstance().getLogger().warning("Failed to batch save default statistics: " + ex.getMessage());
            return 0;
        });
    }

    private static void initTable() {
        DATABASE.executor().create("player_statistics")
            .ifNotExists()
            .column("uuid", "TEXT")
            .column("key", "TEXT")
            .column("value", "TEXT")
            .primaryKey("uuid", "key")
            .execute();
    }
}