package com.github.darksoulq.abyssallib.world.level.data.player;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.database.Database;
import com.github.darksoulq.abyssallib.server.database.impl.sqlite.SqliteDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private static final Map<UUID, PlayerData> CACHE = new HashMap<>();

    private final UUID uuid;
    private final Map<String, String> rawValues = new HashMap<>();
    private static final Database database = new SqliteDatabase(new File(AbyssalLib.getInstance().getDataFolder(),
            "player.db"));

    private PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public static PlayerData of(Player player) {
        return of(player.getUniqueId());
    }

    public static PlayerData of(UUID uuid) {
        return CACHE.computeIfAbsent(uuid, PlayerData::new);
    }

    public <T> void set(Attribute<T> attr, T value) {
        rawValues.put(attr.key(), value.toString());
        save(attr.key(), value.toString());
    }

    public <T> T get(Attribute<T> attr) {
        String raw = rawValues.get(attr.key());
        if (raw == null) return attr.defaultValue();
        return deserialize(raw, attr.type(), attr.defaultValue());
    }

    private <T> T deserialize(String value, Class<T> type, T fallback) {
        try {
            if (type == Integer.class) return type.cast(Integer.parseInt(value));
            if (type == Boolean.class) return type.cast(Boolean.parseBoolean(value));
            if (type == Double.class) return type.cast(Double.parseDouble(value));
            if (type == String.class) return type.cast(value);
        } catch (Exception e) {
            return fallback;
        }
        return fallback;
    }

    public void load() {
        Bukkit.getScheduler().runTaskAsynchronously(AbyssalLib.getInstance(), () -> {
            var rows = database.executor().table("player_data")
                    .where("uuid = ?", uuid.toString())
                    .select(rs -> Map.entry(rs.getString("key"), rs.getString("value")));

            rawValues.clear();
            for (var entry : rows) {
                rawValues.put(entry.getKey(), entry.getValue());
            }
        });
    }

    private void save(String key, String value) {
        Bukkit.getScheduler().runTaskAsynchronously(AbyssalLib.getInstance(), () -> {
            database.executor().table("player_data").insert()
                    .value("uuid", uuid.toString())
                    .value("key", key)
                    .value("value", value)
                    .execute();
        });
    }

    public static void initTable() {
        database.executor().table("player_data").create()
                .ifNotExists()
                .column("uuid", "TEXT")
                .column("key", "TEXT")
                .column("value", "TEXT")
                .primaryKey("uuid", "key")
                .execute();
    }
}
