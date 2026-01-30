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
 * Manages the collection of statistics for a specific player.
 * <p>
 * This class handles asynchronous database I/O, caching, and synchronization
 * between registered statistic templates and persistent player data.
 */
public class PlayerStatistics {
    /** Global cache of loaded player statistics. */
    private static final Map<UUID, PlayerStatistics> CACHE = new ConcurrentHashMap<>();

    /** The SQLite database instance for statistics storage. */
    private static final Database DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "player_statistics.db"));

    /** The unique ID of the player owning these statistics. */
    private final UUID uuid;

    /** The map of identifiers to active statistic instances. */
    private final Map<Identifier, Statistic> stats = new ConcurrentHashMap<>();

    /**
     * Private constructor to initiate statistic loading for a player.
     *
     * @param uuid the player's UUID
     */
    private PlayerStatistics(UUID uuid) {
        this.uuid = uuid;
        load();
    }

    /**
     * Initializes the database connection and creates the statistics table if necessary.
     */
    public static void init() {
        Try.run(() -> {
            DATABASE.connect();
            initTable();
        }).orElseThrow(t -> new RuntimeException("Failed to initialize PlayerStatistics database", t));
    }

    /**
     * Gets or creates the PlayerStatistics container for a Bukkit player.
     *
     * @param player the player
     * @return the player's statistics container
     */
    public static PlayerStatistics of(Player player) {
        return of(player.getUniqueId());
    }

    /**
     * Gets or creates the PlayerStatistics container for a UUID.
     *
     * @param uuid the player's UUID
     * @return the statistics container
     */
    public static PlayerStatistics of(UUID uuid) {
        return CACHE.computeIfAbsent(uuid, PlayerStatistics::new);
    }

    /**
     * Retrieves a specific statistic by its identifier.
     *
     * @param id the identifier of the statistic
     * @return a clone of the statistic, or null if not found
     */
    public Statistic get(Identifier id) {
        Statistic stat = stats.get(id);
        return stat != null ? stat.clone() : null;
    }

    /**
     * Updates or inserts a statistic and triggers an asynchronous save.
     *
     * @param stat the statistic to set
     */
    public void set(Statistic stat) {
        stats.put(stat.getId(), stat.clone());
        save(stat);
    }

    /**
     * Gets all statistics currently held for this player.
     *
     * @return a list of statistics
     */
    public List<Statistic> get() {
        return stats.values().stream().toList();
    }

    /**
     * Triggers an asynchronous database REPLACE query for a single statistic.
     *
     * @param stat the statistic to save
     */
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

    /**
     * Asynchronously loads all statistics from the database.
     * <p>
     * After loading existing rows, it compares them with {@link Registries#STATISTICS}
     * and initializes missing defaults.
     */
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

                    Object value = Try.of(() -> (Object) Codec.oneOf(Codecs.INT, Codecs.FLOAT, Codecs.BOOLEAN)
                            .decode(StringOps.INSTANCE, entry.getValue()))
                        .orElse(template.getValue());

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

    /**
     * Efficiently saves a list of new default statistics using a batch query.
     *
     * @param newStats the list of default statistics to insert
     */
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

    /**
     * Internal method to create the database schema.
     */
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