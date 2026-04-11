package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database;
import com.github.darksoulq.abyssallib.common.serialization.ops.StringOps;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStatistics {

    private static final Map<UUID, PlayerStatistics> CACHE = new ConcurrentHashMap<>();
    private static final Database DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "player_statistics.db"));

    private final UUID uuid;
    private final Map<Statistic, Integer> stats = new ConcurrentHashMap<>();

    private PlayerStatistics(UUID uuid) {
        this.uuid = uuid;
        load();
    }

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

    public static PlayerStatistics of(Player player) {
        return CACHE.computeIfAbsent(player.getUniqueId(), PlayerStatistics::new);
    }

    public int get(Statistic stat) {
        return stats.getOrDefault(stat, 0);
    }

    public void increment(Statistic stat, int amount) {
        set(stat, get(stat) + amount);
    }

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

    private void saveToDb(Statistic stat, int value) {
        String serialized = Statistic.CODEC.encode(StringOps.INSTANCE, stat);
        DATABASE.executor().table("player_stats").replace()
            .value("uuid", uuid.toString())
            .value("stat_id", serialized)
            .value("value", value)
            .executeAsync();
    }

    private void load() {
        DATABASE.executor().table("player_stats")
            .where("uuid = ?", uuid.toString())
            .selectAsync(rs -> {
                try {
                    String statId = rs.getString("stat_id");
                    Statistic stat = Statistic.CODEC.decode(StringOps.INSTANCE, statId);
                    int val = rs.getInt("value");
                    stats.put(stat, val);
                } catch (Exception e) {
                    AbyssalLib.LOGGER.warning("Skipped invalid stat loading for player " + uuid + ": " + e.getMessage());
                }
                return null;
            });
    }

    public Map<Statistic, Integer> getAll() {
        return Map.copyOf(stats);
    }
}