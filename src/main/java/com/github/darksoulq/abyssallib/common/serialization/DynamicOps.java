package com.github.darksoulq.abyssallib.common.serialization;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Defines a set of operations for a specific serialized format (e.g., JSON, YAML, NBT).
 * This abstraction allows {@link Codec}s to remain format-agnostic.
 * @param <T> The base type of the serialized format (e.g., JsonElement, ConfigurationSection).
 */
public abstract class DynamicOps<T> {
    /** @param value String value.
     * @return Serialized string. */
    public abstract T createString(String value);
    /** @param value Integer value.
     * @return Serialized int. */
    public abstract T createInt(int value);
    /** @param value Long value.
     * @return Serialized long. */
    public abstract T createLong(long value);
    /** @param value Float value.
     * @return Serialized float. */
    public abstract T createFloat(float value);
    /** @param value Double value.
     * @return Serialized double. */
    public abstract T createDouble(double value);
    /** @param value Boolean value.
     * @return Serialized boolean. */
    public abstract T createBoolean(boolean value);
    /** @param elements List of serialized elements.
     * @return Serialized list. */
    public abstract T createList(List<T> elements);
    /** @param map Map of serialized key-value pairs.
     * @return Serialized map. */
    public abstract T createMap(Map<T, T> map);

    /** @param input Serialized input.
     * @return Optional string value. */
    public abstract Optional<String> getStringValue(T input);
    /** @param input Serialized input.
     * @return Optional integer value. */
    public abstract Optional<Integer> getIntValue(T input);
    /** @param input Serialized input.
     * @return Optional long value. */
    public abstract Optional<Long> getLongValue(T input);
    /** @param input Serialized input.
     * @return Optional float value. */
    public abstract Optional<Float> getFloatValue(T input);
    /** @param input Serialized input.
     * @return Optional double value. */
    public abstract Optional<Double> getDoubleValue(T input);
    /** @param input Serialized input.
     * @return Optional boolean value. */
    public abstract Optional<Boolean> getBooleanValue(T input);
    /** @param input Serialized input.
     * @return Optional list of elements. */
    public abstract Optional<List<T>> getList(T input);
    /** @param input Serialized input.
     * @return Optional map of elements. */
    public abstract Optional<Map<T, T>> getMap(T input);

    /** @return An object representing a "null" or "empty" state in the serialized format. */
    public abstract T empty();
}