package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.ops.StringOps;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the loading, storing, and tracking of individual player statistics safely
 * integrated with a local relational database instance.
 */
public class PlayerStatistics {

    private static final Map<UUID, PlayerStatistics> CACHE = new ConcurrentHashMap<>();
    private static final Database DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "player_statistics.db"));

    private final UUID uuid;
    private final Map<Statistic, Integer> stats = new ConcurrentHashMap<>();

    private PlayerStatistics(UUID uuid) {
        this.uuid = uuid;
        load();
    }

    /**
     * Initializes the statistics database connection and ensures the necessary tables exist.
     *
     * @throws Exception If database connection or schema creation fails.
     */
    public static void init() throws Exception {
        DATABASE.connect();
        DATABASE.executor().create("player_stats")
            .ifNotExists()
            .column("uuid", "TEXT")
            .column("stat_id", "TEXT")
            .column("value", "INT")
            .primaryKey("uuid", "stat_id")
            .execute();
    }

    /**
     * Retrieves the statistics cache wrapper for a specific player.
     *
     * @param player The Bukkit player.
     * @return The bound PlayerStatistics instance.
     */
    public static PlayerStatistics of(Player player) {
        return CACHE.computeIfAbsent(player.getUniqueId(), PlayerStatistics::new);
    }

    /**
     * Gets the current integer value of a specified statistic.
     *
     * @param stat The target statistic.
     * @return The tracked value, or 0 if unrecorded.
     */
    public int get(Statistic stat) {
        return stats.getOrDefault(stat, 0);
    }

    /**
     * Increments the tracked value of a specified statistic safely dispatching events.
     *
     * @param stat   The target statistic.
     * @param amount The integer amount to append.
     */
    public void increment(Statistic stat, int amount) {
        set(stat, get(stat) + amount);
    }

    /**
     * Overwrites the tracked value of a specified statistic and synchronizes it to the database.
     *
     * @param stat     The target statistic.
     * @param newValue The exact new integer value to set.
     */
    public void set(Statistic stat, int newValue) {
        int oldValue = get(stat);
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            PlayerStatisticChangeEvent<Integer> event = EventBus.post(new PlayerStatisticChangeEvent<>(player, stat, oldValue, newValue));
            if (event.isCancelled()) return;
            newValue = event.getNewValue();
        }

        stats.put(stat, newValue);
        saveToDb(stat, newValue);
    }

    /**
     * Safely encodes and writes the statistical entry actively to the underlying database layout.
     *
     * @param stat  The modified statistic.
     * @param value The active tracked integer value.
     */
    private void saveToDb(Statistic stat, int value) {
        DataResult<String> res = Statistic.CODEC.encode(StringOps.INSTANCE, stat);
        if (res.isError()) {
            AbyssalLib.LOGGER.warning("Failed to serialize stat for database: " + res.error().get());
            return;
        }

        String serialized = res.getOrThrow();
        DATABASE.executor().table("player_stats").replace()
            .value("uuid", uuid.toString())
            .value("stat_id", serialized)
            .value("value", value)
            .executeAsync();
    }

    /**
     * Evaluates existing database states recursively querying the mapped statistical layout.
     */
    private void load() {
        DATABASE.executor().table("player_stats")
            .where("uuid = ?", uuid.toString())
            .selectAsync(rs -> {
                try {
                    String statId = rs.getString("stat_id");
                    DataResult<Statistic> res = Statistic.CODEC.decode(StringOps.INSTANCE, statId);

                    if (res.isError()) {
                        AbyssalLib.LOGGER.warning("Skipped invalid stat loading for player " + uuid + ": " + res.error().get());
                        return null;
                    }

                    Statistic stat = res.getOrThrow();
                    int val = rs.getInt("value");
                    stats.put(stat, val);
                } catch (Exception e) {
                    AbyssalLib.LOGGER.warning("Skipped invalid stat loading for player " + uuid + ": " + e.getMessage());
                }
                return null;
            });
    }

    /**
     * Retrieves an immutable copy of the active in-memory statistics cache.
     *
     * @return The map snapshot of active statistics.
     */
    public Map<Statistic, Integer> getAll() {
        return Map.copyOf(stats);
    }
}