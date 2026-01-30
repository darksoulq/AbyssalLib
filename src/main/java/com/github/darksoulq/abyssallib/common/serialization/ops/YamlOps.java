package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * An implementation of {@link DynamicOps} using SnakeYAML.
 * <p>
 * This class treats the serialized format as a tree of standard Java objects
 * (Maps, Lists, and Primitives), allowing for bidirectional conversion
 * between Java POJOs and YAML data.
 */
public class YamlOps extends DynamicOps<Object> {

    /** The singleton instance of YamlOps. */
    public static final YamlOps INSTANCE = new YamlOps();

    /** The SnakeYAML engine used for parsing and dumping. */
    private static final Yaml yaml = new Yaml();

    /** Private constructor to enforce the singleton pattern. */
    private YamlOps() {}

    /**
     * @param value The string to wrap.
     * @return The raw string object.
     */
    @Override
    public Object createString(String value) {
        return value;
    }

    /**
     * @param value The integer to wrap.
     * @return The raw Integer object.
     */
    @Override
    public Object createInt(int value) {
        return value;
    }

    /**
     * @param value The long to wrap.
     * @return The raw Long object.
     */
    @Override
    public Object createLong(long value) {
        return value;
    }

    /**
     * @param value The float to wrap.
     * @return The raw Float object.
     */
    @Override
    public Object createFloat(float value) {
        return value;
    }

    /**
     * @param value The double to wrap.
     * @return The raw Double object.
     */
    @Override
    public Object createDouble(double value) {
        return value;
    }

    /**
     * @param value The boolean to wrap.
     * @return The raw Boolean object.
     */
    @Override
    public Object createBoolean(boolean value) {
        return value;
    }

    /**
     * Creates a YAML-compatible list.
     * @param elements The elements to include in the list.
     * @return A new {@link ArrayList} containing the elements.
     */
    @Override
    public Object createList(List<Object> elements) {
        return new ArrayList<>(elements);
    }

    /**
     * Creates a YAML-compatible map.
     * @param map The map entries to include.
     * @return A new {@link LinkedHashMap} to preserve entry order.
     */
    @Override
    public Object createMap(Map<Object, Object> map) {
        return new LinkedHashMap<>(map);
    }

    /**
     * @param input The object to check.
     * @return An Optional containing the string if the input is a {@link String}.
     */
    @Override
    public Optional<String> getStringValue(Object input) {
        return input instanceof String s ? Optional.of(s) : Optional.empty();
    }

    /**
     * @param input The object to check.
     * @return An Optional containing the integer if the input is an {@link Integer}.
     */
    @Override
    public Optional<Integer> getIntValue(Object input) {
        return input instanceof Integer i ? Optional.of(i) : Optional.empty();
    }

    /**
     * @param input The object to check.
     * @return An Optional containing the long if the input is a {@link Long}.
     */
    @Override
    public Optional<Long> getLongValue(Object input) {
        return input instanceof Long l ? Optional.of(l) : Optional.empty();
    }

    /**
     * @param input The object to check.
     * @return An Optional containing the float if the input is a {@link Float}.
     */
    @Override
    public Optional<Float> getFloatValue(Object input) {
        return input instanceof Float f ? Optional.of(f) : Optional.empty();
    }

    /**
     * @param input The object to check.
     * @return An Optional containing the double if the input is a {@link Double}.
     */
    @Override
    public Optional<Double> getDoubleValue(Object input) {
        return input instanceof Double d ? Optional.of(d) : Optional.empty();
    }

    /**
     * @param input The object to check.
     * @return An Optional containing the boolean if the input is a {@link Boolean}.
     */
    @Override
    public Optional<Boolean> getBooleanValue(Object input) {
        return input instanceof Boolean b ? Optional.of(b) : Optional.empty();
    }

    /**
     * @param input The object to check.
     * @return An Optional containing the list if the input is a {@link List}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<Object>> getList(Object input) {
        return (input instanceof List<?>) ? Optional.of((List<Object>) input) : Optional.empty();
    }

    /**
     * @param input The object to check.
     * @return An Optional containing the map if the input is a {@link Map}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<Map<Object, Object>> getMap(Object input) {
        return (input instanceof Map<?, ?>) ? Optional.of((Map<Object, Object>) input) : Optional.empty();
    }

    /**
     * @return A {@code null} reference representing an empty YAML node.
     */
    @Override
    public Object empty() {
        return null;
    }

    /**
     * Parses a YAML string into a normalized Java object structure.
     *
     * @param yamlText The raw YAML text.
     * @return A normalized object (Map, List, or Primitive).
     */
    public Object parse(String yamlText) {
        return normalize(yaml.load(yamlText));
    }

    /**
     * Parses YAML data from an InputStream into a normalized Java object structure.
     *
     * @param in The input stream containing YAML data.
     * @return A normalized object.
     */
    public Object parse(InputStream in) {
        return normalize(yaml.load(in));
    }

    /**
     * Serializes a Java object structure into a YAML string.
     *
     * @param obj The object to dump.
     * @return The formatted YAML string.
     */
    public static String dump(Object obj) {
        return yaml.dump(obj);
    }

    /**
     * Recursively normalizes the output of SnakeYAML.
     * <p>
     * Ensures that all map keys are converted to Strings and that all nested
     * collections are converted to {@link LinkedHashMap} and {@link ArrayList}
     * to satisfy the requirements of the Codec system.
     *
     * @param obj The raw object from the YAML parser.
     * @return The normalized object.
     */
    private Object normalize(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            Map<Object, Object> normalized = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                normalized.put(String.join("", String.valueOf(e.getKey())), normalize(e.getValue()));
            }
            return normalized;
        } else if (obj instanceof List<?> list) {
            List<Object> normalized = new ArrayList<>();
            for (Object o : list) {
                normalized.add(normalize(o));
            }
            return normalized;
        } else {
            return obj;
        }
    }
}