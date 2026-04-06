package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.TileState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core abstraction responsible for handling serialization and deserialization of {@link TileState}
 * implementations into a generic, format-agnostic representation.
 *
 * <p>This class provides a pluggable adapter system where concrete implementations define how
 * specific {@link TileState} types are encoded and decoded. Adapters are registered globally
 * and resolved dynamically at runtime based on {@link #doesApply(TileState)}.</p>
 *
 * <p>The system is designed to:
 * <ul>
 *     <li>Support multiple serialization backends via {@link DynamicOps}</li>
 *     <li>Allow multiple adapters per logical key</li>
 *     <li>Gracefully handle failures using {@link Try}</li>
 *     <li>Remain extensible without modifying core logic</li>
 * </ul>
 *
 * @param <T> The concrete {@link TileState} subtype this adapter operates on
 */
public abstract class TileAdapter<T> {

    /**
     * Global registry mapping string identifiers to lists of {@link TileAdapter} instances.
     *
     * <p>Each key represents a logical serialization category (e.g. "chest", "sign").
     * Multiple adapters may exist per key to support polymorphic matching via
     * {@link #doesApply(TileState)}.</p>
     */
    private static final Map<String, List<TileAdapter<?>>> ADAPTERS = new HashMap<>();

    /**
     * Determines whether this adapter is applicable to the provided {@link TileState}.
     *
     * <p>This method is used during both serialization and deserialization to select
     * the correct adapter implementation.</p>
     *
     * @param state The {@link TileState} instance to evaluate
     * @return {@code true} if this adapter can handle the given state, otherwise {@code false}
     */
    public abstract boolean doesApply(TileState state);

    /**
     * Serializes the given {@link TileState} instance into a generic representation.
     *
     * <p>The output type is defined by the provided {@link DynamicOps} implementation,
     * allowing support for formats such as JSON, NBT, etc.</p>
     *
     * @param ops   The dynamic operations instance defining the output format
     * @param value The concrete {@link TileState} instance to serialize
     * @param <D>   The generic data type produced by the serialization process
     * @return The serialized representation of the given state
     *
     * @throws Codec.CodecException If serialization fails due to invalid data or encoding issues
     */
    public abstract <D> D serialize(DynamicOps<D> ops, T value) throws Codec.CodecException;

    /**
     * Deserializes the given generic input into the provided {@link TileState} instance.
     *
     * <p>This method mutates the {@code base} instance directly rather than creating a new object.</p>
     *
     * @param ops   The dynamic operations instance defining the input format
     * @param input The serialized data to decode
     * @param base  The target {@link TileState} instance to apply decoded values to
     * @param <D>   The generic data type representing the serialized format
     *
     * @throws Codec.CodecException If deserialization fails due to invalid or incompatible data
     */
    public abstract <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException;

    /**
     * Internal helper used to safely invoke {@link #serialize(DynamicOps, Object)} with
     * proper generic casting.
     *
     * <p>This method suppresses unchecked warnings because adapter type safety is guaranteed
     * by {@link #doesApply(TileState)}.</p>
     *
     * @param adapter The adapter performing serialization
     * @param ops     The dynamic operations instance
     * @param state   The {@link TileState} to serialize
     * @param <D>     The output data type
     * @param <T>     The adapter's expected TileState subtype
     * @return The serialized representation
     *
     * @throws Codec.CodecException If serialization fails
     */
    @SuppressWarnings("unchecked")
    private static <D, T> D serialize(TileAdapter<T> adapter, DynamicOps<D> ops, TileState state) throws Codec.CodecException {
        return adapter.serialize(ops, (T) state);
    }

    /**
     * Serializes all applicable data from the provided {@link TileState} using registered adapters.
     *
     * <p>For each registered key:
     * <ul>
     *     <li>Adapters are iterated in order</li>
     *     <li>The first matching adapter ({@link #doesApply(TileState)}) is used</li>
     *     <li>The result is stored under the corresponding key</li>
     * </ul>
     *
     * <p>Failures are safely ignored via {@link Try}, allowing partial serialization.</p>
     *
     * @param ops   The dynamic operations instance defining output format
     * @param state The {@link TileState} to serialize
     * @param <D>   The output data type
     * @return A map containing serialized data keyed by adapter identifiers
     */
    public static <D> Map<D, D> save(DynamicOps<D> ops, TileState state) {
        Map<D, D> values = new HashMap<>();

        for (Map.Entry<String, List<TileAdapter<?>>> entry : ADAPTERS.entrySet()) {
            for (TileAdapter<?> adapter : entry.getValue()) {
                if (!adapter.doesApply(state)) continue;

                D serialized = Try.of(() -> serialize(adapter, ops, state)).orElse(null);

                if (serialized != null) {
                    values.put(ops.createString(entry.getKey()), serialized);
                    break;
                }
            }
        }

        return values;
    }

    /**
     * Applies serialized data onto a {@link TileState} instance using registered adapters.
     *
     * <p>For each entry:
     * <ul>
     *     <li>The key is resolved to a string</li>
     *     <li>Matching adapters are retrieved</li>
     *     <li>The first successful {@link #deserialize(DynamicOps, Object, TileState)} is applied</li>
     * </ul>
     *
     * <p>Errors are safely ignored using {@link Try}, ensuring robustness.</p>
     *
     * @param ops   The dynamic operations instance defining input format
     * @param input The serialized data map
     * @param base  The target {@link TileState} instance to modify
     * @param <D>   The input data type
     */
    public static <D> void load(DynamicOps<D> ops, Map<D, D> input, TileState base) {
        for (Map.Entry<D, D> entry : input.entrySet()) {
            String key = Try.of(() -> ops.getStringValue(entry.getKey()).orElseThrow()).orElse(null);

            List<TileAdapter<?>> adapters = ADAPTERS.get(key);
            if (adapters == null) continue;

            for (TileAdapter<?> adapter : adapters) {
                if (!adapter.doesApply(base)) continue;

                if (Try.run(() -> adapter.deserialize(ops, entry.getValue(), base)).isSuccess()) {
                    break;
                }
            }
        }
    }

    /**
     * Registers a {@link TileAdapter} under a specific key.
     *
     * <p>This replaces any existing adapters for the given key.</p>
     *
     * @param key     The unique identifier used during serialization (e.g. "chest", "sign")
     * @param adapter The adapter instance to register
     */
    public static void register(String key, TileAdapter<?>... adapter) {
        ADAPTERS.put(key, List.of(adapter));
    }
}