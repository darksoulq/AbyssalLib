package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.common.config.DataPath;

import java.util.*;
import java.util.function.Function;

/**
 * Provides format-specific operations for creating, reading, and transforming
 * serialized data structures.
 * <p>
 * Implementations define how primitive values, lists, and maps are represented
 * in a particular serialization format such as JSON, YAML, NBT, or binary data.
 * This abstraction allows {@link Codec} implementations to operate independently
 * of the underlying serialized representation.
 *
 * @param <T> the serialized value type used by the format
 */
public abstract class DynamicOps<T> {

    /**
     * Indicates whether this format prefers compressed map representations.
     *
     * @return {@code true} if compressed map encoding should be used,
     * otherwise {@code false}
     */
    public boolean compressMaps() {
        return false;
    }

    /**
     * Returns the format-specific representation of an empty value.
     *
     * @return the empty value representation
     */
    public abstract T empty();

    /**
     * Creates an empty map value.
     *
     * @return an empty serialized map
     */
    public T emptyMap() {
        return createMap(Map.of());
    }

    /**
     * Creates an empty list value.
     *
     * @return an empty serialized list
     */
    public T emptyList() {
        return createList(List.of());
    }

    /**
     * Creates a serialized string value.
     *
     * @param value the string to serialize
     * @return the serialized string
     */
    public abstract T createString(String value);

    /**
     * Creates a serialized byte value.
     *
     * @param value the byte to serialize
     * @return the serialized byte
     */
    public abstract T createByte(byte value);

    /**
     * Creates a serialized short value.
     *
     * @param value the short to serialize
     * @return the serialized short
     */
    public abstract T createShort(short value);

    /**
     * Creates a serialized integer value.
     *
     * @param value the integer to serialize
     * @return the serialized integer
     */
    public abstract T createInt(int value);

    /**
     * Creates a serialized long value.
     *
     * @param value the long to serialize
     * @return the serialized long
     */
    public abstract T createLong(long value);

    /**
     * Creates a serialized float value.
     *
     * @param value the float to serialize
     * @return the serialized float
     */
    public abstract T createFloat(float value);

    /**
     * Creates a serialized double value.
     *
     * @param value the double to serialize
     * @return the serialized double
     */
    public abstract T createDouble(double value);

    /**
     * Creates a serialized boolean value.
     *
     * @param value the boolean to serialize
     * @return the serialized boolean
     */
    public abstract T createBoolean(boolean value);

    /**
     * Creates a serialized list.
     *
     * @param elements the serialized list elements
     * @return the serialized list
     */
    public abstract T createList(List<T> elements);

    /**
     * Creates a serialized map.
     *
     * @param map the serialized key-value pairs
     * @return the serialized map
     */
    public abstract T createMap(Map<T, T> map);

    /**
     * Attempts to read a string value.
     *
     * @param input the serialized value
     * @return the decoded string, if present
     */
    public abstract Optional<String> getStringValue(T input);

    /**
     * Attempts to read a numeric value.
     *
     * @param input the serialized value
     * @return the decoded number, if present
     */
    public abstract Optional<Number> getNumberValue(T input);

    /**
     * Attempts to read an integer value.
     *
     * @param input the serialized value
     * @return the decoded integer, if present
     */
    public abstract Optional<Integer> getIntValue(T input);

    /**
     * Attempts to read a long value.
     *
     * @param input the serialized value
     * @return the decoded long, if present
     */
    public abstract Optional<Long> getLongValue(T input);

    /**
     * Attempts to read a float value.
     *
     * @param input the serialized value
     * @return the decoded float, if present
     */
    public abstract Optional<Float> getFloatValue(T input);

    /**
     * Attempts to read a double value.
     *
     * @param input the serialized value
     * @return the decoded double, if present
     */
    public abstract Optional<Double> getDoubleValue(T input);

    /**
     * Attempts to read a boolean value.
     *
     * @param input the serialized value
     * @return the decoded boolean, if present
     */
    public abstract Optional<Boolean> getBooleanValue(T input);

    /**
     * Attempts to read a list value.
     *
     * @param input the serialized value
     * @return the decoded list, if the value is a list
     */
    public abstract Optional<List<T>> getList(T input);

    /**
     * Attempts to read a map value.
     *
     * @param input the serialized value
     * @return the decoded map, if the value is a map
     */
    public abstract Optional<Map<T, T>> getMap(T input);

    /**
     * Returns the string keys contained in a map value.
     *
     * @param input the serialized value
     * @return the map keys, if the value is a map
     */
    public abstract Optional<Iterable<String>> getKeys(T input);

    /**
     * Returns the number of elements contained in a list or map value.
     *
     * @param input the serialized value
     * @return the collection size, if applicable
     */
    public abstract OptionalInt size(T input);

    /**
     * Creates a deep copy of the specified serialized value.
     *
     * @param input the value to copy
     * @return a copy that is independent of the original
     */
    public abstract T copy(T input);

    /**
     * Appends a value to a serialized list.
     * <p>
     * If the supplied value is not already a list, a new list containing both
     * the original value and the appended value is created.
     *
     * @param list the target list value
     * @param value the value to append
     * @return the resulting list
     */
    public T mergeToList(T list, T value) {
        Optional<List<T>> listOpt = getList(list);
        if (listOpt.isPresent()) {
            List<T> mutable = new ArrayList<>(listOpt.get());
            mutable.add(value);
            return createList(mutable);
        }
        return createList(List.of(list, value));
    }

    /**
     * Inserts or replaces an entry in a serialized map.
     * <p>
     * If the supplied value is not already a map, a new map containing only
     * the provided key-value pair is created.
     *
     * @param map the target map value
     * @param key the entry key
     * @param value the entry value
     * @return the resulting map
     */
    public T mergeToMap(T map, T key, T value) {
        Optional<Map<T, T>> mapOpt = getMap(map);
        if (mapOpt.isPresent()) {
            Map<T, T> mutable = new LinkedHashMap<>(mapOpt.get());
            mutable.put(key, value);
            return createMap(mutable);
        }
        Map<T, T> newMap = new LinkedHashMap<>();
        newMap.put(key, value);
        return createMap(newMap);
    }

    /**
     * Determines whether a value exists at the specified path.
     *
     * @param input the root value
     * @param path the path to check
     * @return {@code true} if a value exists at the path
     */
    public boolean exists(T input, String path) {
        return query(input, path).isPresent();
    }

    /**
     * Determines whether a value exists at the specified path.
     *
     * @param input the root value
     * @param path the compiled path to check
     * @return {@code true} if a value exists at the path
     */
    public boolean exists(T input, DataPath path) {
        return query(input, path).isPresent();
    }

    /**
     * Retrieves a nested value using a path expression.
     *
     * @param input the root value
     * @param path the path expression
     * @return the value at the path, if present
     */
    public Optional<T> query(T input, String path) {
        return query(input, DataPath.of(path));
    }

    /**
     * Retrieves a nested value using a compiled path.
     *
     * @param input the root value
     * @param path the compiled path
     * @return the value at the path, if present
     */
    public Optional<T> query(T input, DataPath path) {
        T current = input;
        for (DataPath.Segment segment : path.segments()) {
            if (segment instanceof DataPath.Key(String value1)) {
                Optional<Map<T, T>> mapOpt = getMap(current);
                if (mapOpt.isPresent()) {
                    T val = mapOpt.get().get(createString(value1));
                    if (val != null) {
                        current = val;
                        continue;
                    }
                }
                return Optional.empty();
            } else if (segment instanceof DataPath.Index(int value)) {
                Optional<List<T>> listOpt = getList(current);
                if (listOpt.isPresent()) {
                    List<T> list = listOpt.get();
                    if (value >= 0 && value < list.size()) {
                        current = list.get(value);
                        continue;
                    }
                }
                return Optional.empty();
            }
        }
        return Optional.of(current);
    }

    /**
     * Sets a value at the specified path, creating intermediate containers as
     * needed.
     *
     * @param input the root value
     * @param path the path expression
     * @param value the value to store
     * @return the updated root value
     */
    public T set(T input, String path, T value) {
        return set(input, DataPath.of(path), value);
    }

    /**
     * Sets a value at the specified path, creating intermediate containers as
     * needed.
     *
     * @param input the root value
     * @param path the compiled path
     * @param value the value to store
     * @return the updated root value
     */
    public T set(T input, DataPath path, T value) {
        if (path.isEmpty()) return value;
        return buildTree(input, path.segments(), 0, value);
    }

    private T buildTree(T current, List<DataPath.Segment> segments, int index, T targetValue) {
        if (index >= segments.size()) return targetValue;

        DataPath.Segment segment = segments.get(index);

        if (segment instanceof DataPath.Key(String value1)) {
            Optional<Map<T, T>> mapOpt = getMap(current);
            Map<T, T> map = mapOpt.map(LinkedHashMap::new).orElseGet(LinkedHashMap::new);

            T keyObj = createString(value1);
            T existing = map.getOrDefault(keyObj, empty());

            map.put(keyObj, buildTree(existing, segments, index + 1, targetValue));
            return createMap(map);
        } else if (segment instanceof DataPath.Index(int value)) {
            Optional<List<T>> listOpt = getList(current);
            List<T> list = listOpt.map(ArrayList::new).orElseGet(ArrayList::new);

            while (list.size() <= value) {
                list.add(empty());
            }

            T existing = list.get(value);
            list.set(value, buildTree(existing, segments, index + 1, targetValue));
            return createList(list);
        }

        return empty();
    }

    /**
     * Applies a transformation to the value located at the specified path.
     * <p>
     * Missing intermediate containers are created as needed. The operation
     * follows copy-on-write semantics and returns a new root value containing
     * the modification.
     *
     * @param input the root value
     * @param path the path expression
     * @param editor the function used to transform the target value
     * @return the updated root value
     */
    public T edit(T input, String path, Function<T, T> editor) {
        return edit(input, DataPath.of(path), editor);
    }

    /**
     * Applies a transformation to the value located at the specified path.
     * <p>
     * Missing intermediate containers are created as needed. The operation
     * follows copy-on-write semantics and returns a new root value containing
     * the modification.
     *
     * @param input the root value
     * @param path the compiled path
     * @param editor the function used to transform the target value
     * @return the updated root value
     */
    public T edit(T input, DataPath path, Function<T, T> editor) {
        if (path.isEmpty()) return editor.apply(input);
        return editTree(input, path.segments(), 0, editor);
    }

    /**
     * Recursively traverses the structure and applies an editor function to the
     * target node.
     *
     * @param current the current value being traversed
     * @param segments the path segments
     * @param index the current segment index
     * @param editor the transformation function
     * @return the updated value
     */
    private T editTree(T current, List<DataPath.Segment> segments, int index, Function<T, T> editor) {
        if (index >= segments.size()) return editor.apply(current);
        DataPath.Segment segment = segments.get(index);

        if (segment instanceof DataPath.Key(String value)) {
            Optional<Map<T, T>> mapOpt = getMap(current);
            Map<T, T> map = mapOpt.map(LinkedHashMap::new).orElseGet(LinkedHashMap::new);
            T keyObj = createString(value);
            T existing = map.getOrDefault(keyObj, empty());
            map.put(keyObj, editTree(existing, segments, index + 1, editor));
            return createMap(map);
        } else if (segment instanceof DataPath.Index(int value)) {
            Optional<List<T>> listOpt = getList(current);
            List<T> list = listOpt.map(ArrayList::new).orElseGet(ArrayList::new);
            while (list.size() <= value) list.add(empty());
            T existing = list.get(value);
            list.set(value, editTree(existing, segments, index + 1, editor));
            return createList(list);
        }
        return current;
    }

    /**
     * Removes the value located at the specified path.
     *
     * @param input the root value
     * @param path the path expression
     * @return the updated root value
     */
    public T remove(T input, String path) {
        return remove(input, DataPath.of(path));
    }

    /**
     * Removes the value located at the specified path.
     *
     * @param input the root value
     * @param path the compiled path
     * @return the updated root value
     */
    public T remove(T input, DataPath path) {
        if (path.isEmpty()) return empty();
        return removeTree(input, path.segments(), 0);
    }

    /**
     * Recursively traverses the structure and removes the target value.
     *
     * @param current the current value being traversed
     * @param segments the path segments
     * @param index the current segment index
     * @return the updated value
     */
    private T removeTree(T current, List<DataPath.Segment> segments, int index) {
        if (index >= segments.size()) return empty();
        DataPath.Segment segment = segments.get(index);
        boolean isLast = index == segments.size() - 1;

        if (segment instanceof DataPath.Key(String value)) {
            Optional<Map<T, T>> mapOpt = getMap(current);
            if (mapOpt.isEmpty()) return current;
            Map<T, T> map = new LinkedHashMap<>(mapOpt.get());
            T keyObj = createString(value);

            if (isLast) {
                map.remove(keyObj);
            } else if (map.containsKey(keyObj)) {
                map.put(keyObj, removeTree(map.get(keyObj), segments, index + 1));
            }
            return createMap(map);
        } else if (segment instanceof DataPath.Index(int value)) {
            Optional<List<T>> listOpt = getList(current);
            if (listOpt.isEmpty()) return current;
            List<T> list = new ArrayList<>(listOpt.get());

            if (value >= 0 && value < list.size()) {
                if (isLast) {
                    list.remove(value);
                } else {
                    list.set(value, removeTree(list.get(value), segments, index + 1));
                }
            }
            return createList(list);
        }
        return current;
    }

    /**
     * Converts a serialized value from this format into another format.
     * <p>
     * Primitive values, lists, and maps are recursively translated using the
     * target {@link DynamicOps} implementation.
     *
     * @param <R> the target serialized value type
     * @param outOps the target operations implementation
     * @param input the value to convert
     * @return the converted value
     */
    public <R> R convertTo(DynamicOps<R> outOps, T input) {
        if (input == null || input.equals(empty())) return outOps.empty();
        Optional<String> s = getStringValue(input);
        if (s.isPresent()) return outOps.createString(s.get());
        Optional<Boolean> b = getBooleanValue(input);
        if (b.isPresent()) return outOps.createBoolean(b.get());
        Optional<Double> d = getDoubleValue(input);
        if (d.isPresent()) return outOps.createDouble(d.get());
        Optional<Float> f = getFloatValue(input);
        if (f.isPresent()) return outOps.createFloat(f.get());
        Optional<Long> l = getLongValue(input);
        if (l.isPresent()) return outOps.createLong(l.get());
        Optional<Integer> i = getIntValue(input);
        if (i.isPresent()) return outOps.createInt(i.get());
        Optional<List<T>> list = getList(input);
        if (list.isPresent()) {
            List<R> outList = new ArrayList<>();
            for (T item : list.get()) outList.add(convertTo(outOps, item));
            return outOps.createList(outList);
        }
        Optional<Map<T, T>> map = getMap(input);
        if (map.isPresent()) {
            Map<R, R> outMap = new LinkedHashMap<>();
            for (Map.Entry<T, T> entry : map.get().entrySet()) {
                outMap.put(convertTo(outOps, entry.getKey()), convertTo(outOps, entry.getValue()));
            }
            return outOps.createMap(outMap);
        }
        return outOps.empty();
    }
}