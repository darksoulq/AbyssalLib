package com.github.darksoulq.abyssallib.world.data.attribute;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityAttributeChangeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityAttributeModifierAddEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityAttributeModifierRemoveEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages custom attributes and modifiers for entities with persistent storage support.
 * This class acts as a bridge between the SQLite database and dynamic {@link AttributeInstance}s.
 * Only supports attributes explicitly registered in {@link Registries#ATTRIBUTES}.
 */
public class EntityAttributes {
    /** Cache of loaded attribute containers indexed by entity UUID. */
    private static final Map<UUID, EntityAttributes> CACHE = new ConcurrentHashMap<>();
    /** The unique identifier of the entity being managed. */
    private final UUID uuid;
    /** Cache of active attribute instances for this entity. */
    private final Map<Key, AttributeInstance> instances = new ConcurrentHashMap<>();
    /** Map of raw base values loaded from the database. */
    private final Map<String, Double> rawBaseValues = new ConcurrentHashMap<>();

    private static final Database DATABASE = new Database(
        new File(AbyssalLib.getInstance().getDataFolder(), "entity_data.db")
    );

    /**
     * Initializes the global attribute database and ensures the table structure exists.
     *
     * @throws RuntimeException If the database connection or table initialization fails.
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
     * @param uuid The {@link UUID} of the target entity.
     */
    private EntityAttributes(UUID uuid) {
        this.uuid = uuid;
        load();
    }

    /**
     * Retrieves the attribute container for a specific entity.
     *
     * @param entity The {@link Entity} instance.
     * @return The associated {@link EntityAttributes} manager.
     */
    public static EntityAttributes of(Entity entity) {
        return of(entity.getUniqueId());
    }

    /**
     * Retrieves the attribute container for a specific UUID.
     *
     * @param uuid The {@link UUID} of the target entity.
     * @return The associated {@link EntityAttributes} manager.
     */
    public static EntityAttributes of(UUID uuid) {
        return CACHE.computeIfAbsent(uuid, EntityAttributes::new);
    }

    private void validate(Attribute attr) {
        if (Registries.ATTRIBUTES.get(attr.key().asString()) == null) {
            throw new IllegalArgumentException("Cannot interact with unregistered attribute: " + attr.key().asString());
        }
    }

    /**
     * Retrieves or creates the active instance for a given registered attribute.
     *
     * @param attr The {@link Attribute} definition.
     * @return The {@link AttributeInstance}.
     * @throws IllegalArgumentException If the attribute is not registered.
     */
    public AttributeInstance getInstance(Attribute attr) {
        validate(attr);
        return instances.computeIfAbsent(attr.key(), k -> {
            double base = rawBaseValues.getOrDefault(attr.key().asString(), attr.defaultValue());
            return new AttributeInstance(attr, base);
        });
    }

    /**
     * Sets the base value of an attribute, triggering a change event and updating the database.
     *
     * @param attr  The {@link Attribute} definition to update.
     * @param value The new base value to assign.
     * @throws IllegalArgumentException If the attribute is not registered.
     */
    public void setBaseValue(Attribute attr, double value) {
        validate(attr);
        Entity entity = Bukkit.getEntity(uuid);
        if (entity != null) {
            double oldBase = getBaseValue(attr);
            EntityAttributeChangeEvent event = EventBus.post(new EntityAttributeChangeEvent(entity, attr, oldBase, value));
            if (event.isCancelled()) {
                return;
            }
            value = event.getNewValue();
        }

        getInstance(attr).setBaseValue(value);
        rawBaseValues.put(attr.key().asString(), value);
        save(attr.key().asString(), String.valueOf(value));
    }

    /**
     * Adds a modifier to an attribute, allowing for event-driven logic to override the application.
     *
     * @param attr     The {@link Attribute} instance to modify.
     * @param modifier The {@link AttributeModifier} determining how the value interacts with the base.
     * @throws IllegalArgumentException If the attribute is not registered.
     */
    public void addModifier(Attribute attr, AttributeModifier modifier) {
        validate(attr);
        Entity entity = Bukkit.getEntity(uuid);
        AttributeModifier modObj = modifier;

        if (entity != null) {
            EntityAttributeModifierAddEvent event = EventBus.post(new EntityAttributeModifierAddEvent(entity, attr, modObj));
            if (event.isCancelled()) {
                return;
            }
            modObj = event.getModifier();
        }

        getInstance(attr).addModifier(modObj);
    }

    /**
     * Removes a specific modifier from an attribute based on its unique Key.
     *
     * @param attr The {@link Attribute} instance to update.
     * @param id   The unique {@link Key} of the modifier to remove.
     * @throws IllegalArgumentException If the attribute is not registered.
     */
    public void removeModifier(Attribute attr, Key id) {
        validate(attr);
        AttributeInstance instance = getInstance(attr);
        AttributeModifier existing = instance.getModifier(id);

        if (existing == null) {
            return;
        }

        Entity entity = Bukkit.getEntity(uuid);
        if (entity != null) {
            EntityAttributeModifierRemoveEvent event = EventBus.post(new EntityAttributeModifierRemoveEvent(entity, attr, existing));
            if (event.isCancelled()) {
                return;
            }
        }

        instance.removeModifier(id);
    }

    /**
     * Checks if a specific attribute has been defined or loaded for this entity.
     *
     * @param attr The {@link Attribute} to check for.
     * @return True if the attribute exists in the local data map or has an active instance.
     * @throws IllegalArgumentException If the attribute is not registered.
     */
    public boolean has(Attribute attr) {
        validate(attr);
        return rawBaseValues.containsKey(attr.key().asString()) || instances.containsKey(attr.key());
    }

    /**
     * Retrieves the raw base value of an attribute without applying any modifiers.
     *
     * @param attr The {@link Attribute} to retrieve.
     * @return The deserialized base value.
     * @throws IllegalArgumentException If the attribute is not registered.
     */
    public double getBaseValue(Attribute attr) {
        return getInstance(attr).getBaseValue();
    }

    /**
     * Retrieves the final calculated value of an attribute after applying all modifiers.
     *
     * @param attr The {@link Attribute} to retrieve.
     * @return The final calculated value.
     * @throws IllegalArgumentException If the attribute is not registered.
     */
    public double getValue(Attribute attr) {
        return getInstance(attr).getValue();
    }

    /**
     * Retrieves a map containing all raw attribute keys and values for this entity.
     *
     * @return A {@link Map} of string-serialized attribute data.
     */
    public Map<String, String> getAllAttributes() {
        Map<String, String> result = new ConcurrentHashMap<>();
        rawBaseValues.forEach((k, v) -> result.put(k, String.valueOf(v)));
        return result;
    }

    /**
     * Asynchronously loads all attribute data for this entity from the database.
     * Only loads data for attributes currently present in the registry.
     */
    public void load() {
        DATABASE.executor().table("entity_data")
            .where("uuid = ?", uuid.toString())
            .selectAsync(rs -> Map.entry(rs.getString("key"), rs.getString("value")))
            .thenAccept(rows -> {
                Map<String, Double> temp = new ConcurrentHashMap<>();
                for (var entry : rows) {
                    if (Registries.ATTRIBUTES.get(entry.getKey()) != null) {
                        try {
                            temp.put(entry.getKey(), Double.parseDouble(entry.getValue()));
                        } catch (NumberFormatException ignored) {}
                    }
                }
                rawBaseValues.clear();
                rawBaseValues.putAll(temp);

                for (Map.Entry<Key, AttributeInstance> entry : instances.entrySet()) {
                    Double dbBase = rawBaseValues.get(entry.getKey().asString());
                    if (dbBase != null) {
                        entry.getValue().setBaseValue(dbBase);
                    }
                }
            })
            .exceptionally(ex -> {
                AbyssalLib.getInstance().getLogger().severe("Failed to load attributes for " + uuid + ": " + ex.getMessage());
                return null;
            });
    }

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