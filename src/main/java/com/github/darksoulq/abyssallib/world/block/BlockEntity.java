package com.github.darksoulq.abyssallib.world.block;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.block.property.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the entity data associated with a custom {@link CustomBlock}.
 * <p>
 * A {@code BlockEntity} stores additional state and behavior for a block that requires more than just
 * a material type.
 * </p>
 * <p>
 * This is an abstract base class intended to be extended by specific block entity implementations.
 * </p>
 */
public abstract class BlockEntity {

    /**
     * The {@link CustomBlock} instance this entity is associated with.
     */
    private final CustomBlock block;

    /**
     * Constructs a new {@code BlockEntity} linked to the given {@link CustomBlock}.
     *
     * @param block the block this entity belongs to
     */
    public BlockEntity(CustomBlock block) {
        this.block = block;
    }

    /**
     * Returns the block that this entity is associated with.
     *
     * @return the associated {@link CustomBlock}
     */
    public CustomBlock getBlock() {
        return block;
    }

    /**
     * This method is called every tick
     */
    public void serverTick() {}

    /**
     * This method is called randomly similar to how minecraft ticks crops
     */
    public void randomTick() {}

    /**
     * Called after this entity is loaded from persistent storage (deserialized).
     * <p>
     * Subclasses can override this method to perform any necessary initialization
     * or data processing after loading.
     * </p>
     */
    public void onLoad() {}

    /**
     * Called before this entity is saved to persistent storage (serialized).
     * <p>
     * Subclasses can override this method to update or prepare data prior to saving.
     * </p>
     */
    public void onSave() {}

    /**
     * Serializes all {@link Property} fields of this BlockEntity.
     */
    public <D> D serialize(DynamicOps<D> ops) throws Exception {
        Map<D, D> map = new LinkedHashMap<>();

        Class<?> cls = getClass();
        while (cls != BlockEntity.class && cls != Object.class) {
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
     * Deserializes all {@link Property} fields of this BlockEntity.
     */
    public <D> void deserialize(DynamicOps<D> ops, D input) throws Exception {
        Map<D, D> map = ops.getMap(input).orElse(Map.of());

        Class<?> cls = getClass();
        while (cls != BlockEntity.class && cls != Object.class) {
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
