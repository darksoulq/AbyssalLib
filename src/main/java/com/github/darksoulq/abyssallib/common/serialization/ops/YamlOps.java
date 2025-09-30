package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * {@link DynamicOps} implementation for YAML serialization and deserialization.
 * <p>
 * Provides encoding and decoding of primitive types, lists, and maps in a format compatible with SnakeYAML.
 * Includes helper methods for parsing YAML from strings or input streams and dumping objects to YAML strings.
 * Normalizes maps and lists recursively to ensure consistent types.
 * <p>
 * Singleton implementation; use {@link #INSTANCE}.
 */
public class YamlOps extends DynamicOps<Object> {

    /** Singleton instance of {@link YamlOps}. */
    public static final YamlOps INSTANCE = new YamlOps();

    /** Internal SnakeYAML instance for parsing and dumping YAML. */
    private final Yaml yaml = new Yaml();

    /** Private constructor to enforce singleton usage. */
    private YamlOps() {}

    /** {@inheritDoc} Returns the input string as-is. */
    @Override
    public Object createString(String value) {
        return value;
    }

    /** {@inheritDoc} Returns the input int as-is. */
    @Override
    public Object createInt(int value) {
        return value;
    }

    /** {@inheritDoc} Returns the input long as-is. */
    @Override
    public Object createLong(long value) {
        return value;
    }

    /** {@inheritDoc} Returns the input float as-is. */
    @Override
    public Object createFloat(float value) {
        return value;
    }

    /** {@inheritDoc} Returns the input double as-is. */
    @Override
    public Object createDouble(double value) {
        return value;
    }

    /** {@inheritDoc} Returns the input boolean as-is. */
    @Override
    public Object createBoolean(boolean value) {
        return value;
    }

    /** {@inheritDoc} Returns a new {@link ArrayList} copy of the input elements. */
    @Override
    public Object createList(List<Object> elements) {
        return new ArrayList<>(elements);
    }

    /** {@inheritDoc} Returns a new {@link LinkedHashMap} copy of the input map. */
    @Override
    public Object createMap(Map<Object, Object> map) {
        return new LinkedHashMap<>(map);
    }

    /** {@inheritDoc} Decodes a string value if the input is a {@link String}. */
    @Override
    public Optional<String> getStringValue(Object input) {
        return input instanceof String s ? Optional.of(s) : Optional.empty();
    }

    /** {@inheritDoc} Decodes an int value if the input is an {@link Integer}. */
    @Override
    public Optional<Integer> getIntValue(Object input) {
        return input instanceof Integer i ? Optional.of(i) : Optional.empty();
    }

    /** {@inheritDoc} Decodes a long value if the input is a {@link Long}. */
    @Override
    public Optional<Long> getLongValue(Object input) {
        return input instanceof Long l ? Optional.of(l) : Optional.empty();
    }

    /** {@inheritDoc} Decodes a float value if the input is a {@link Float}. */
    @Override
    public Optional<Float> getFloatValue(Object input) {
        return input instanceof Float f ? Optional.of(f) : Optional.empty();
    }

    /** {@inheritDoc} Decodes a double value if the input is a {@link Double}. */
    @Override
    public Optional<Double> getDoubleValue(Object input) {
        return input instanceof Double d ? Optional.of(d) : Optional.empty();
    }

    /** {@inheritDoc} Decodes a boolean value if the input is a {@link Boolean}. */
    @Override
    public Optional<Boolean> getBooleanValue(Object input) {
        return input instanceof Boolean b ? Optional.of(b) : Optional.empty();
    }

    /** {@inheritDoc} Decodes a list if the input is a {@link List}. */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<Object>> getList(Object input) {
        return (input instanceof List<?>) ? Optional.of((List<Object>) input) : Optional.empty();
    }

    /** {@inheritDoc} Decodes a map if the input is a {@link Map}. */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<Map<Object, Object>> getMap(Object input) {
        return (input instanceof Map<?, ?>) ? Optional.of((Map<Object, Object>) input) : Optional.empty();
    }

    /** {@inheritDoc} Returns {@code null} for an empty value. */
    @Override
    public Object empty() {
        return null;
    }

    /**
     * Parses a YAML string into a normalized Java object (maps and lists recursively normalized).
     *
     * @param yamlText the YAML text to parse
     * @return normalized Java representation of the YAML
     */
    public Object parse(String yamlText) {
        return normalize(yaml.load(yamlText));
    }

    /**
     * Parses a YAML input stream into a normalized Java object (maps and lists recursively normalized).
     *
     * @param in the input stream containing YAML
     * @return normalized Java representation of the YAML
     */
    public Object parse(InputStream in) {
        return normalize(yaml.load(in));
    }

    /**
     * Dumps a Java object into a YAML string.
     *
     * @param obj the object to dump
     * @return YAML string representation
     */
    public String dump(Object obj) {
        return yaml.dump(obj);
    }

    /**
     * Recursively normalizes an object by converting nested maps and lists to {@link LinkedHashMap} and {@link ArrayList}.
     *
     * @param obj the object to normalize
     * @return normalized object
     */
    private Object normalize(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            Map<Object, Object> normalized = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                normalized.put(String.valueOf(e.getKey()), normalize(e.getValue()));
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
