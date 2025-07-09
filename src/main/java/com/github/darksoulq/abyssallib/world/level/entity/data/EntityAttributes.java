package com.github.darksoulq.abyssallib.world.level.entity.data;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.database.Database;
import com.github.darksoulq.abyssallib.server.database.impl.sqlite.SqliteDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents persistent per-entity attribute data. Provides access to attributes stored in memory
 * and persisted in a local database. This class is thread-safe.
 */
public class EntityAttributes {

    /**
     * A thread-safe cache of loaded EntityAttributes instances, keyed by entity UUID.
     */
    private static final Map<UUID, EntityAttributes> CACHE = new ConcurrentHashMap<>();

    /**
     * The UUID of the entity this data belongs to.
     */
    private final UUID uuid;

    /**
     * A thread-safe map of raw string values for each attribute key.
     */
    private final Map<String, String> rawValues = new ConcurrentHashMap<>();

    /**
     * The backing SQLite database for attribute persistence.
     */
    private static final Database DATABASE = new SqliteDatabase(
            new File(AbyssalLib.getInstance().getDataFolder(), "entity_data.db")
    );

    public static void init() {
        try {
            DATABASE.connect();
            initTable();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Private constructor. Use {@link #of(UUID)} to create or fetch a PlayerData instance.
     *
     * @param uuid the UUID of the player
     */
    private EntityAttributes(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the EntityAttributes for the given {@link Entity} instance.
     *
     * @param entity the entity
     * @return the entity's data wrapper
     */
    public static EntityAttributes of(Entity entity) {
        return of(entity.getUniqueId());
    }

    /**
     * Gets the EntityAttributes for the given entity UUID, loading from memory or creating a new instance.
     *
     * @param uuid the entity's UUID
     * @return the EntityAttributes instance
     */
    public static EntityAttributes of(UUID uuid) {
        return CACHE.computeIfAbsent(uuid, EntityAttributes::new);
    }

    /**
     * Stores a value for a given attribute and persists it to the database.
     *
     * @param attr  the attribute to store
     * @param value the value to assign
     * @param <T>   the type of the attribute
     */
    public <T extends Number> void set(Attribute<T> attr, T value) {
        rawValues.put(attr.key(), value.toString());
        save(attr.key(), value.toString());
    }

    /**
     * Checks whether this player has stored a value for the given attribute.
     *
     * @param attr the attribute to check
     * @return true if the attribute exists, false otherwise
     */
    public boolean has(Attribute<?> attr) {
        return rawValues.containsKey(attr.key());
    }

    /**
     * Gets the base (unmodified) value of the specified attribute.
     * Returns {@code null} if the value is not stored.
     *
     * @param attr the attribute to fetch
     * @param <T>  the type of the attribute
     * @return the raw stored value without modifiers, or {@code null} if not present
     */
    public <T extends Number> T getBaseValue(Attribute<T> attr) {
        String raw = rawValues.get(attr.key());
        if (raw == null) return null;
        return deserialize(raw, attr.type(), null);
    }

    /**
     * Retrieves the value for the given attribute, applying any registered modifiers.
     * If no value is stored, returns {@code null}.
     *
     * @param attr the attribute to fetch
     * @param <T>  the type of the attribute
     * @return the stored and modified value, or {@code null} if none
     */
    public <T extends Number> T get(Attribute<T> attr) {
        String raw = rawValues.get(attr.key());
        if (raw == null) return null;
        T base = deserialize(raw, attr.type(), null);
        return attr.applyModifiers(base);
    }

    /**
     * Retrieves all the Attributes that are currrently on the entity.
     *
     * @return List of all attributes
     */
    public Map<String, String> getAllAttributes() {
        return rawValues;
    }

    /**
     * Deserializes a string value into the specified type.
     *
     * @param value    the raw string value
     * @param type     the expected Java class
     * @param fallback the fallback value to return on failure
     * @param <T>      the generic type
     * @return the parsed value, or fallback if invalid
     */
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

    /**
     * Loads all attribute data for this entity asynchronously from the database.
     */
    public void load() {
        Bukkit.getScheduler().runTaskAsynchronously(AbyssalLib.getInstance(), () -> {
            var rows = DATABASE.executor().table("entity_data")
                    .where("uuid = ?", uuid.toString())
                    .select(rs -> Map.entry(rs.getString("key"), rs.getString("value")));

            Map<String, String> temp = new ConcurrentHashMap<>();
            for (var entry : rows) {
                temp.put(entry.getKey(), entry.getValue());
            }

            rawValues.clear();
            rawValues.putAll(temp);
        });
    }

    /**
     * Persists a key-value attribute pair to the database asynchronously.
     *
     * @param key   the attribute key
     * @param value the attribute value as string
     */
    private void save(String key, String value) {
        Bukkit.getScheduler().runTaskAsynchronously(AbyssalLib.getInstance(), () -> {
            DATABASE.executor().table("entity_data").insert()
                    .value("uuid", uuid.toString())
                    .value("key", key)
                    .value("value", value)
                    .execute();
        });
    }

    /**
     * Initializes the entity_data database table with required schema if it doesn't already exist.
     */
    public static void initTable() {
        DATABASE.executor().table("entity_data").create()
                .ifNotExists()
                .column("uuid", "TEXT")
                .column("key", "TEXT")
                .column("value", "TEXT")
                .primaryKey("uuid", "key")
                .execute();
    }
}
