package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.Database;
import com.github.darksoulq.abyssallib.common.database.impl.sqlite.SqliteDatabase;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles statistics for individual players, including loading, saving,
 * and caching of player statistics.
 */
public class PlayerStatistics {
    /** Cache of all loaded player statistics mapped by player UUID. */
    private static final Map<UUID, PlayerStatistics> CACHE = new ConcurrentHashMap<>();
    /** The database used to persist player statistics. */
    private static Database DATABASE = new SqliteDatabase(
            new File(AbyssalLib.getInstance().getDataFolder(), "player_statistics.db")
    );

    /** The UUID of the player this statistics object belongs to. */
    private final UUID uuid;
    /** Map of statistics for this player, keyed by their identifier. */
    private final Map<Identifier, Statistic> stats = new ConcurrentHashMap<>();

    private PlayerStatistics(UUID uuid) {
        this.uuid = uuid;
        load();
    }

    /**
     * Initializes the player statistics system and connects to the database.
     * Also ensures the database table exists.
     */
    public static void init() {
        try {
            DATABASE.connect();
            initTable();
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    /**
     * Sets a statistic for this player and saves it asynchronously to the database.
     *
     * @param stat the statistic to set
     */
    public void set(Statistic stat) {
        stats.put(stat.getId(), stat.clone());
        save(stat);
    }

    private void save(Statistic stat) {
        Bukkit.getScheduler().runTaskAsynchronously(AbyssalLib.getInstance(), () -> {
            DATABASE.executor().table("player_statistics").insert()
                    .value("uuid", uuid.toString())
                    .value("key", stat.getId().toString())
                    .value("value", stat.getValue().toString())
                    .execute();
        });
    }

    private void load() {
        Bukkit.getScheduler().runTaskAsynchronously(AbyssalLib.getInstance(), () -> {
            var rows = DATABASE.executor().table("player_statistics")
                    .where("uuid = ?", uuid.toString())
                    .select(rs -> Map.entry(rs.getString("key"), rs.getString("value")));

            Map<Identifier, Statistic> temp = new ConcurrentHashMap<>();
            if (rows.isEmpty()) {
                Registries.STATISTICS.getAll().forEach((key, stat) -> {
                    temp.put(Identifier.of(key), stat.clone());
                });
            }
            for (var entry : rows) {
                Identifier id = Identifier.of(entry.getKey());

                if (!Registries.STATISTICS.contains(id.toString())) {
                    AbyssalLib.getInstance().getLogger().warning("Statistic not registered: " + id);
                    continue;
                }

                Statistic template = Registries.STATISTICS.get(id.toString()).clone();
                Object value = deserialize(entry.getValue(), template.getClass(), template.getValue());
                template.setValue(value);
                temp.put(id, template);
            }

            stats.clear();
            stats.putAll(temp);
        });
    }

    private <T> T deserialize(String value, Class<? extends Statistic> type, Object fallback) {
        try {
            if (Statistic.IntStatistic.class.isAssignableFrom(type))
                return (T) Integer.valueOf(value);
            if (Statistic.FloatStatistic.class.isAssignableFrom(type))
                return (T) Float.valueOf(value);
            if (Statistic.BooleanStatistic.class.isAssignableFrom(type))
                return (T) Boolean.valueOf(value);
        } catch (Exception ignored) {
        }
        return (T) fallback;
    }

    private static void initTable() {
        DATABASE.executor().table("player_statistics").create()
                .ifNotExists()
                .column("uuid", "TEXT")
                .column("key", "TEXT")
                .column("value", "TEXT")
                .primaryKey("uuid", "key")
                .execute();
    }
}
