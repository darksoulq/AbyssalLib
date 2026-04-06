package com.github.darksoulq.abyssallib.common.serialization.internal.entity;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract adapter responsible for handling serialization and deserialization
 * of specific {@link Entity} types.
 *
 * <p>This system enables modular, type-specific encoding and decoding of entity
 * state by delegating logic to registered adapter implementations.</p>
 *
 * <p>Adapters are registered globally and are conditionally applied based on
 * {@link #doesApply(Entity)} checks.</p>
 *
 * @param <T> the specific entity type this adapter supports
 */
public abstract class EntityAdapter<T> {

    /**
     * Global registry of all active entity adapters.
     *
     * <p>Adapters are evaluated in registration order during serialization
     * and deserialization passes.</p>
     */
    private static final List<EntityAdapter<?>> ADAPTERS = new ArrayList<>();

    /**
     * Determines whether this adapter should be applied to the given {@link Entity}.
     *
     * @param entity the entity to evaluate
     * @return {@code true} if this adapter supports the entity, otherwise {@code false}
     */
    public abstract boolean doesApply(Entity entity);

    /**
     * Serializes the given entity into the provided map using the supplied {@link DynamicOps}.
     *
     * @param ops   the dynamic operations instance used for encoding
     * @param value the entity instance to serialize
     * @param map   the target map to populate with encoded values
     * @param <D>   the encoded data type
     * @throws Codec.CodecException if encoding fails
     */
    public abstract <D> void serialize(DynamicOps<D> ops, T value, Map<D, D> map) throws Codec.CodecException;

    /**
     * Deserializes data from the provided map and applies it onto the given entity.
     *
     * @param ops  the dynamic operations instance used for decoding
     * @param map  the input data map
     * @param base the target entity to apply decoded values to
     * @param <D>  the encoded data type
     * @throws Codec.CodecException if decoding fails
     */
    public abstract <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException;

    /**
     * Internal helper method for safely invoking a typed adapter's serialize logic.
     *
     * @param adapter the adapter instance
     * @param ops     the dynamic operations instance
     * @param entity  the entity to serialize
     * @param map     the output map
     * @param <D>     the encoded data type
     * @param <T>     the adapter's supported entity type
     * @throws Codec.CodecException if serialization fails
     */
    @SuppressWarnings("unchecked")
    private static <D, T> void serialize(EntityAdapter<T> adapter, DynamicOps<D> ops, Entity entity, Map<D, D> map) throws Codec.CodecException {
        adapter.serialize(ops, (T) entity, map);
    }

    /**
     * Serializes an {@link Entity} into a map representation using all applicable adapters.
     *
     * <p>Each registered adapter is evaluated via {@link #doesApply(Entity)}, and if applicable,
     * its serialization logic is executed.</p>
     *
     * <p>Failures during individual adapter execution are safely suppressed.</p>
     *
     * @param ops    the dynamic operations instance used for encoding
     * @param entity the entity to serialize
     * @param <D>    the encoded data type
     * @return a map containing serialized entity data
     */
    public static <D> Map<D, D> save(DynamicOps<D> ops, Entity entity) {
        Map<D, D> values = new HashMap<>();
        for (EntityAdapter<?> adapter : ADAPTERS) {
            if (adapter.doesApply(entity)) {
                Try.run(() -> serialize(adapter, ops, entity, values));
            }
        }
        return values;
    }

    /**
     * Deserializes and applies data onto an {@link Entity} using all applicable adapters.
     *
     * <p>Each registered adapter is evaluated via {@link #doesApply(Entity)}, and if applicable,
     * its deserialization logic is executed.</p>
     *
     * <p>Failures during individual adapter execution are safely suppressed.</p>
     *
     * @param ops   the dynamic operations instance used for decoding
     * @param input the input data map
     * @param base  the target entity
     * @param <D>   the encoded data type
     */
    public static <D> void load(DynamicOps<D> ops, Map<D, D> input, Entity base) {
        for (EntityAdapter<?> adapter : ADAPTERS) {
            if (adapter.doesApply(base)) {
                Try.run(() -> adapter.deserialize(ops, input, base));
            }
        }
    }

    /**
     * Registers a new {@link EntityAdapter} into the global adapter registry.
     *
     * <p>Adapters are applied in the order they are registered.</p>
     *
     * @param adapter the adapter to register
     */
    public static void register(EntityAdapter<?> adapter) {
        ADAPTERS.add(adapter);
    }
}