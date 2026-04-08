package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.relational.sql.BatchQuery;
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ops.StringOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the collection and persistence of statistics for a specific player.
 * This class handles asynchronous database I/O, event dispatching for value changes,
 * and maintains a local cache for performance.
 */
public class PlayerStatistics {

    /** Cache of loaded statistics indexed by player UUID. */
    private static final Map<UUID, PlayerStatistics> CACHE = new ConcurrentHashMap<>();

    /** The database instance used for persistent storage of player data. */
    private static final Database DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "player_statistics.db"));

    /** The unique identifier of the player being managed. */
    private final UUID uuid;

    /** Thread-safe map of active statistics for this player. */
    private final Map<Key, Statistic<?>> stats = new ConcurrentHashMap<>();

    /**
     * Private constructor for initializing a player's statistics container.
     *
     * @param uuid
     * The {@link UUID} of the player.
     */
    private PlayerStatistics(UUID uuid) {
        this.uuid = uuid;
        load();
    }

    /**
     * Initializes the global statistics database and its required tables.
     */
    public static void init() {
        Try.run(() -> {
            DATABASE.connect();
            initTable();
        }).orElseThrow(t -> new RuntimeException("Failed to initialize PlayerStatistics database", t));
    }

    /**
     * Retrieves the statistics container for a specific player.
     *
     * @param player
     * The {@link Player} instance.
     * @return
     * The associated {@link PlayerStatistics} manager.
     */
    public static PlayerStatistics of(Player player) {
        return of(player.getUniqueId());
    }

    /**
     * Retrieves the statistics container for a specific UUID.
     *
     * @param uuid
     * The {@link UUID} of the target player.
     * @return
     * The associated {@link PlayerStatistics} manager.
     */
    public static PlayerStatistics of(UUID uuid) {
        return CACHE.computeIfAbsent(uuid, PlayerStatistics::new);
    }

    /**
     * Retrieves a cloned instance of a specific statistic by its Key.
     *
     * @param id
     * The {@link Key} of the statistic to find.
     * @return
     * A copy of the {@link Statistic}, or null if not found.
     */
    public Statistic<?> get(Key id) {
        Statistic<?> stat = stats.get(id);
        return stat != null ? stat.clone() : null;
    }

    /**
     * Updates a player's statistic, triggering a change event and saving to the database.
     *
     * @param <T>
     * The value type of the statistic.
     * @param stat
     * The new {@link Statistic} instance to set.
     */
    @SuppressWarnings("unchecked")
    public <T> void set(Statistic<T> stat) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            Statistic<T> oldStat = (Statistic<T>) stats.get(stat.getId());
            T oldValue = oldStat != null ? oldStat.getValue() : null;
            T newValue = stat.getValue();

            PlayerStatisticChangeEvent<T> event = EventBus.post(new PlayerStatisticChangeEvent<>(player, stat, oldValue, newValue));
            if (event.isCancelled()) {
                return;
            }
            stat.setValue(event.getNewValue());
        }

        stats.put(stat.getId(), stat.clone());
        save(stat);
    }

    /**
     * Retrieves an immutable list of all statistics currently tracked for this player.
     *
     * @return
     * A {@link List} containing all player {@link Statistic} instances.
     */
    public List<Statistic<?>> get() {
        return stats.values().stream().toList();
    }

    /**
     * Asynchronously saves a specific statistic to the database.
     *
     * @param stat
     * The {@link Statistic} to save.
     */
    private void save(Statistic<?> stat) {
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
     * Internal logic to load player statistics from the database and populate defaults.
     */
    @SuppressWarnings("unchecked")
    private void load() {
        DATABASE.executor().table("player_statistics")
            .where("uuid = ?", uuid.toString())
            .selectAsync(rs -> Map.entry(rs.getString("key"), rs.getString("value")))
            .thenAccept(rows -> {
                Map<Key, Statistic<?>> tempStats = new ConcurrentHashMap<>();
                List<Statistic<?>> newDefaultsToSave = new ArrayList<>();
                for (var entry : rows) {
                    Key id = Key.key(entry.getKey());

                    if (!Registries.STATISTICS.contains(id.toString())) {
                        AbyssalLib.getInstance().getLogger().warning("Statistic not registered: " + id);
                        continue;
                    }

                    Statistic<Object> template = (Statistic<Object>) Registries.STATISTICS.get(id.toString()).clone();

                    Object value = Try.of(() -> (Object) Codec.oneOf(Codecs.INT, Codecs.FLOAT, Codecs.BOOLEAN)
                            .decode(StringOps.INSTANCE, entry.getValue()))
                        .orElse(template.getValue());

                    template.setValue(value);
                    tempStats.put(id, template);
                }

                Registries.STATISTICS.getAll().forEach((key, template) -> {
                    Key id = Key.key(key);
                    if (!tempStats.containsKey(id)) {
                        Statistic<?> clone = template.clone();
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
     * Batches multiple new statistics into a single database insertion for efficiency.
     *
     * @param newStats
     * The list of {@link Statistic} instances to batch.
     */
    private void saveDefaultsBatch(List<Statistic<?>> newStats) {
        if (newStats.isEmpty()) {
            return;
        }

        BatchQuery batch = DATABASE.executor().table("player_statistics")
            .batch("uuid", "key", "value")
            .insertIgnore();

        for (Statistic<?> stat : newStats) {
            batch.add(uuid.toString(), stat.getId().toString(), stat.getValue().toString());
        }

        batch.executeAsync().exceptionally(ex -> {
            AbyssalLib.getInstance().getLogger().warning("Failed to batch save default statistics: " + ex.getMessage());
            return 0;
        });
    }

    /**
     * Creates the necessary SQLite table structure for statistics storage.
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