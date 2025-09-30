package com.github.darksoulq.abyssallib.common.serialization;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract interface representing operations on a dynamic data structure.
 * <p>
 * Provides methods to create, read, and manipulate primitive values, lists, and maps.
 * Used by {@link Codec} to abstract over different serialization formats.
 *
 * @param <T> the underlying representation type
 */
public abstract class DynamicOps<T> {
    /** Creates a string value. */
    public abstract T createString(String value);
    /** Creates an integer value. */
    public abstract T createInt(int value);
    /** Creates a long value. */
    public abstract T createLong(long value);
    /** Creates a float value. */
    public abstract T createFloat(float value);
    /** Creates a double value. */
    public abstract T createDouble(double value);
    /** Creates a boolean value. */
    public abstract T createBoolean(boolean value);
    /** Creates a list from elements. */
    public abstract T createList(List<T> elements);
    /** Creates a map from key-value pairs. */
    public abstract T createMap(Map<T, T> map);

    /** Extracts a string value if possible. */
    public abstract Optional<String> getStringValue(T input);
    /** Extracts an integer value if possible. */
    public abstract Optional<Integer> getIntValue(T input);
    /** Extracts a long value if possible. */
    public abstract Optional<Long> getLongValue(T input);
    /** Extracts a float value if possible. */
    public abstract Optional<Float> getFloatValue(T input);
    /** Extracts a double value if possible. */
    public abstract Optional<Double> getDoubleValue(T input);
    /** Extracts a boolean value if possible. */
    public abstract Optional<Boolean> getBooleanValue(T input);
    /** Extracts a list value if possible. */
    public abstract Optional<List<T>> getList(T input);
    /** Extracts a map value if possible. */
    public abstract Optional<Map<T, T>> getMap(T input);

    /**
     * Returns a representation of an empty value in this dynamic type.
     *
     * @return the empty value
     */
    public abstract T empty();
}
