package com.github.darksoulq.abyssallib.world.entity;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.block.property.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A base class for entities that utilize the property-based serialization system.
 * <p>
 * This class uses reflection to automatically discover {@link Property} fields
 * and handle their persistence. Subclasses can define properties as fields,
 * and they will be automatically saved to and loaded from the world data.
 *
 * @param <T> the type of the parent/owner of this entity (e.g., CustomBlock)
 */
public abstract class AbstractPropertyEntity<T> {

    /** The type or owner associated with this entity.
     */
    private final T type;

    /**
     * Constructs a new AbstractPropertyEntity.
     *
     * @param type the associated type or owner
     */
    public AbstractPropertyEntity(T type) {
        this.type = type;
    }

    /**
     * Gets the associated type or owner of this entity.
     *
     * @return the owner instance
     */
    public T getType() {
        return type;
    }

    /**
     * Called every server tick. Override to provide active logic.
     */
    public void serverTick() {}

    /**
     * Called during a random world tick.
     */
    public void randomTick() {}

    /**
     * Called when the entity is loaded into the world.
     */
    public void onLoad() {}

    /**
     * Called before the entity is saved to the world data.
     */
    public void onSave() {}

    /**
     * Serializes all discovered properties into a dynamic map.
     * <p>
     * This method iterates through all declared fields in the class and its
     * superclasses (up to {@code AbstractPropertyEntity}), identifying
     * {@link Property} instances and encoding them.
     *
     * @param ops the dynamic operations logic
     * @param <D> the data format type
     * @return a serialized map of property names to values
     * @throws Exception if reflection or encoding fails
     */
    public <D> D serialize(DynamicOps<D> ops) throws Exception {
        Map<D, D> map = new LinkedHashMap<>();
        Class<?> cls = getClass();
        while (cls != AbstractPropertyEntity.class && cls != Object.class) {
            for (Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                field.setAccessible(true);
                Object obj = field.get(this);

                if (obj instanceof Property<?> prop) {
                    D encoded = prop.encode(ops);
                    map.put(ops.createString(field.getName()), encoded);
                }
            }
            cls = cls.getSuperclass();
        }
        return ops.createMap(map);
    }

    /**
     * Deserializes properties from a dynamic map into this instance.
     * <p>
     * Matches keys in the serialized map to field names in the class hierarchy
     * that are instances of {@link Property}.
     *
     * @param ops   the dynamic operations logic
     * @param input the serialized map data
     * @param <D>   the data format type
     * @throws Exception if reflection or decoding fails
     */
    public <D> void deserialize(DynamicOps<D> ops, D input) throws Exception {
        Map<D, D> map = ops.getMap(input).orElse(Map.of());
        Class<?> cls = getClass();
        while (cls != AbstractPropertyEntity.class && cls != Object.class) {
            for (Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                field.setAccessible(true);
                Object obj = field.get(this);

                if (!(obj instanceof Property<?> prop)) continue;

                D encoded = map.get(ops.createString(field.getName()));
                if (encoded != null) {
                    prop.decode(ops, encoded);
                }
            }
            cls = cls.getSuperclass();
        }
    }
}