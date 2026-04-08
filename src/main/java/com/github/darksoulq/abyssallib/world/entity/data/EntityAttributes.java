package com.github.darksoulq.abyssallib.world.entity.data;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityAttributeChangeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityAttributeModifierAddEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityAttributeModifierRemoveEvent;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages custom attributes and modifiers for entities with persistent storage support.
 * This class provides a bridge between raw database values and functional {@link Attribute}
 * objects, allowing for complex arithmetic modifications and event-driven updates.
 */
public class EntityAttributes {

    /** Cache of loaded attribute containers indexed by entity UUID. */
    private static final Map<UUID, EntityAttributes> CACHE = new ConcurrentHashMap<>();

    /** The unique identifier of the entity being managed. */
    private final UUID uuid;

    /** Map of raw attribute keys to their string-serialized values. */
    private final Map<String, String> rawValues = new ConcurrentHashMap<>();

    /** The database instance used for persistent storage of entity attributes. */
    private static final Database DATABASE = new Database(
        new File(AbyssalLib.getInstance().getDataFolder(), "entity_data.db")
    );

    /**
     * Initializes the global attribute database and ensures the table structure exists.
     *
     * @throws RuntimeException
     * If the database connection or table initialization fails.
     */
    public static void init() {
        try {
            DATABASE.connect();
            initTable();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EntityAttributes database", e);
        }
    }

    /**
     * Private constructor for initializing an entity's attribute container.
     *
     * @param uuid
     * The {@link UUID} of the target entity.
     */
    private EntityAttributes(UUID uuid) {
        this.uuid = uuid;
        load();
    }

    /**
     * Retrieves the attribute container for a specific entity.
     *
     * @param entity
     * The {@link Entity} instance.
     * @return
     * The associated {@link EntityAttributes} manager.
     */
    public static EntityAttributes of(Entity entity) {
        return of(entity.getUniqueId());
    }

    /**
     * Retrieves the attribute container for a specific UUID.
     *
     * @param uuid
     * The {@link UUID} of the target entity.
     * @return
     * The associated {@link EntityAttributes} manager.
     */
    public static EntityAttributes of(UUID uuid) {
        return CACHE.computeIfAbsent(uuid, EntityAttributes::new);
    }

    /**
     * Sets the base value of an attribute, triggering a change event and updating the database.
     *
     * @param <T>
     * The numeric type of the attribute.
     * @param attr
     * The {@link Attribute} definition to update.
     * @param value
     * The new base value to assign.
     */
    public <T extends Number> void set(Attribute<T> attr, T value) {
        Entity entity = Bukkit.getEntity(uuid);
        if (entity != null) {
            T oldBase = getBaseValue(attr);
            EntityAttributeChangeEvent<T> event = EventBus.post(new EntityAttributeChangeEvent<>(entity, attr, oldBase, value));
            if (event.isCancelled()) {
                return;
            }
            value = event.getNewValue();
        }

        rawValues.put(attr.key(), value.toString());
        save(attr.key(), value.toString());
    }

    /**
     * Adds a modifier to an attribute, allowing for event-driven logic to override the application.
     *
     * @param <T>
     * The numeric type of the attribute and modifier.
     * @param attr
     * The {@link Attribute} instance to modify.
     * @param id
     * The unique {@link Key} identifying this specific modifier.
     * @param modifier
     * The value of the modifier.
     * @param operation
     * The {@link AttributeOperation} determining how the modifier is applied.
     */
    public <T extends Number> void addModifier(Attribute<T> attr, Key id, T modifier, AttributeOperation operation) {
        Entity entity = Bukkit.getEntity(uuid);
        AttributeModifier<T> modObj = new AttributeModifier<>(modifier, operation);
        if (entity != null) {
            EntityAttributeModifierAddEvent<T> event = EventBus.post(new EntityAttributeModifierAddEvent<>(entity, attr, id, modObj));
            if (event.isCancelled()) {
                return;
            }
            modObj = event.getModifier();
        }
        attr.addModifier(id, modObj.getValue(), modObj.getOperation());
    }

    /**
     * Removes a specific modifier from an attribute based on its unique Key.
     *
     * @param <T>
     * The numeric type of the attribute.
     * @param attr
     * The {@link Attribute} instance to update.
     * @param id
     * The unique {@link Key} of the modifier to remove.
     */
    public <T extends Number> void removeModifier(Attribute<T> attr, Key id) {
        Entity entity = Bukkit.getEntity(uuid);
        AttributeModifier<T> existing = attr.getModifiers().get(id);
        if (existing == null) {
            return;
        }

        if (entity != null) {
            EntityAttributeModifierRemoveEvent<T> event = EventBus.post(new EntityAttributeModifierRemoveEvent<>(entity, attr, id, existing));
            if (event.isCancelled()) {
                return;
            }
        }
        attr.removeModifier(id);
    }

    /**
     * Checks if a specific attribute has been defined or loaded for this entity.
     *
     * @param attr
     * The {@link Attribute} to check for.
     * @return
     * True if the attribute key exists in the local data map.
     */
    public boolean has(Attribute<?> attr) {
        return rawValues.containsKey(attr.key());
    }

    /**
     * Retrieves the raw base value of an attribute without applying any modifiers.
     *
     * @param <T>
     * The numeric type of the attribute.
     * @param attr
     * The {@link Attribute} to retrieve.
     * @return
     * The deserialized base value, or null if not found.
     */
    public <T extends Number> T getBaseValue(Attribute<T> attr) {
        String raw = rawValues.get(attr.key());
        if (raw == null) {
            return null;
        }
        return deserialize(raw, attr.type());
    }

    /**
     * Retrieves the final calculated value of an attribute after applying all modifiers.
     *
     * @param <T>
     * The numeric type of the attribute.
     * @param attr
     * The {@link Attribute} to retrieve.
     * @return
     * The final calculated value, or null if the base value is missing.
     */
    public <T extends Number> T get(Attribute<T> attr) {
        String raw = rawValues.get(attr.key());
        if (raw == null) {
            return null;
        }
        T base = deserialize(raw, attr.type());
        return attr.applyModifiers(base);
    }

    /**
     * Retrieves a map containing all raw attribute keys and values for this entity.
     *
     * @return
     * A {@link Map} of string-serialized attribute data.
     */
    public Map<String, String> getAllAttributes() {
        return rawValues;
    }

    /**
     * Internal logic for deserializing raw database strings into numeric types.
     *
     * @param <T>
     * The target type.
     * @param value
     * The raw string value.
     * @param type
     * The target class type.
     * @return
     * The deserialized object, or null if parsing fails.
     */
    @SuppressWarnings("unchecked")
    private <T> T deserialize(String value, Class<T> type) {
        try {
            if (type == Integer.class) {
                return (T) Integer.valueOf(value);
            }
            if (type == Double.class) {
                return (T) Double.valueOf(value);
            }
            if (type == Float.class) {
                return (T) Float.valueOf(value);
            }
            if (type == Long.class) {
                return (T) Long.valueOf(value);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Asynchronously loads all attribute data for this entity from the database.
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
     * Asynchronously saves an attribute's raw value to the database.
     *
     * @param key
     * The attribute key string.
     * @param value
     * The string-serialized value.
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
     * Initializes the SQLite table used for persistent attribute storage.
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