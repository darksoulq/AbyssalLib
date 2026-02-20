package com.github.darksoulq.abyssallib.world.entity.data;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityAttributes {
    private static final Map<UUID, EntityAttributes> CACHE = new ConcurrentHashMap<>();
    private final UUID uuid;
    private final Map<String, String> rawValues = new ConcurrentHashMap<>();
    private static final Database DATABASE = new Database(
        new File(AbyssalLib.getInstance().getDataFolder(), "entity_data.db")
    );

    public static void init() {
        try {
            DATABASE.connect();
            initTable();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EntityAttributes database", e);
        }
    }

    /**
     * Private constructor. Use {@link #of(UUID)} to create or fetch a PlayerData instance.
     *
     * @param uuid the UUID of the player
     */
    private EntityAttributes(UUID uuid) {
        this.uuid = uuid;
        load();
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
        return deserialize(raw, attr.type());
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
        T base = deserialize(raw, attr.type());
        return attr.applyModifiers(base);
    }

    /**
     * Retrieves all the Attributes that are currently on the entity.
     *
     * @return Map of all attributes
     */
    public Map<String, String> getAllAttributes() {
        return rawValues;
    }

    /**
     * Deserializes a string value into the specified type.
     *
     * @param <T>   the generic type
     * @param value the raw string value
     * @param type  the expected Java class
     * @return the parsed value, or fallback if invalid
     */
    private <T> T deserialize(String value, Class<T> type) {
        try {
            if (type == Integer.class) return type.cast(Integer.parseInt(value));
            if (type == Double.class) return type.cast(Double.parseDouble(value));
            if (type == String.class) return type.cast(value);
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Loads all attribute data for this entity asynchronously from the database.
     */
    public void load() {
        DATABASE.executor().table("entity_data")
            .where("uuid = ?", uuid.toString())
            .selectAsync(rs -> Map.entry(rs.getString("key"), rs.getString("value")))
            .thenAccept(rows -> {
                Map<String, String> temp = new ConcurrentHashMap<>();
                for (var entry : rows) {
                    temp.put(entry.getKey(), entry.getValue());
                }
                rawValues.clear();
                rawValues.putAll(temp);
            })
            .exceptionally(ex -> {
                AbyssalLib.getInstance().getLogger().severe("Failed to load attributes for " + uuid + ": " + ex.getMessage());
                return null;
            });
    }

    /**
     * Persists a key-value attribute pair to the database asynchronously.
     * Uses replace (UPSERT) logic to handle both inserts and updates.
     *
     * @param key   the attribute key
     * @param value the attribute value as string
     */
    private void save(String key, String value) {
        DATABASE.executor().table("entity_data").replace()
            .value("uuid", uuid.toString())
            .value("key", key)
            .value("value", value)
            .executeAsync()
            .exceptionally(ex -> {
                AbyssalLib.getInstance().getLogger().warning("Failed to save attribute " + key + " for " + uuid + ": " + ex.getMessage());
                return 0;
            });
    }

    /**
     * Initializes the entity_data database table with required schema if it doesn't already exist.
     */
    public static void initTable() {
        DATABASE.executor().create("entity_data")
            .ifNotExists()
            .column("uuid", "TEXT")
            .column("key", "TEXT")
            .column("value", "TEXT")
            .primaryKey("uuid", "key")
            .execute();
    }
}