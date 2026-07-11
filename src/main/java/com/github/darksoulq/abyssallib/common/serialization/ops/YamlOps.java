package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@link DynamicOps} implementation backed by SnakeYAML.
 * <p>
 * This implementation represents YAML data as plain Java objects (Map, List, primitives),
 * enabling serialization and deserialization of hierarchical structures in a generic form.
 */
public class YamlOps extends DynamicOps<Object> {

    /**
     * Singleton instance of {@code YamlOps}.
     */
    public static final YamlOps INSTANCE = new YamlOps();

    /**
     * SnakeYAML parser instance used for loading and dumping YAML.
     */
    private static final Yaml yaml = new Yaml();

    /**
     * Creates a new {@code YamlOps} instance.
     */
    private YamlOps() {
    }

    /**
     * Creates a string value.
     *
     * @param value the string
     * @return the same string object
     */
    @Override
    public Object createString(String value) {
        return value;
    }

    /**
     * Creates a byte value.
     *
     * @param value the byte
     * @return boxed byte
     */
    @Override
    public Object createByte(byte value) {
        return value;
    }

    /**
     * Creates a short value.
     *
     * @param value the short
     * @return boxed short
     */
    @Override
    public Object createShort(short value) {
        return value;
    }

    /**
     * Creates an int value.
     *
     * @param value the int
     * @return boxed integer
     */
    @Override
    public Object createInt(int value) {
        return value;
    }

    /**
     * Creates a long value.
     *
     * @param value the long
     * @return boxed long
     */
    @Override
    public Object createLong(long value) {
        return value;
    }

    /**
     * Creates a float value.
     *
     * @param value the float
     * @return boxed float
     */
    @Override
    public Object createFloat(float value) {
        return value;
    }

    /**
     * Creates a double value.
     *
     * @param value the double
     * @return boxed double
     */
    @Override
    public Object createDouble(double value) {
        return value;
    }

    /**
     * Creates a boolean value.
     *
     * @param value the boolean
     * @return boxed boolean
     */
    @Override
    public Object createBoolean(boolean value) {
        return value;
    }

    /**
     * Creates a YAML list.
     *
     * @param elements list elements
     * @return mutable ArrayList containing elements
     */
    @Override
    public Object createList(List<Object> elements) {
        return new ArrayList<>(elements);
    }

    /**
     * Creates a YAML map.
     *
     * @param map key-value pairs
     * @return ordered LinkedHashMap copy
     */
    @Override
    public Object createMap(Map<Object, Object> map) {
        return new LinkedHashMap<>(map);
    }

    /**
     * Extracts a string value if possible.
     *
     * @param input input object
     * @return optional string
     */
    @Override
    public Optional<String> getStringValue(Object input) {
        return input instanceof String s ? Optional.of(s) : Optional.empty();
    }

    /**
     * Extracts a numeric value if possible.
     *
     * @param input input object
     * @return optional number
     */
    @Override
    public Optional<Number> getNumberValue(Object input) {
        return input instanceof Number n ? Optional.of(n) : Optional.empty();
    }

    /**
     * Extracts an integer value if possible.
     *
     * @param input input object
     * @return optional int
     */
    @Override
    public Optional<Integer> getIntValue(Object input) {
        return input instanceof Number n ? Optional.of(n.intValue()) : Optional.empty();
    }

    /**
     * Extracts a long value if possible.
     *
     * @param input input object
     * @return optional long
     */
    @Override
    public Optional<Long> getLongValue(Object input) {
        return input instanceof Number n ? Optional.of(n.longValue()) : Optional.empty();
    }

    /**
     * Extracts a float value if possible.
     *
     * @param input input object
     * @return optional float
     */
    @Override
    public Optional<Float> getFloatValue(Object input) {
        return input instanceof Number n ? Optional.of(n.floatValue()) : Optional.empty();
    }

    /**
     * Extracts a double value if possible.
     *
     * @param input input object
     * @return optional double
     */
    @Override
    public Optional<Double> getDoubleValue(Object input) {
        return input instanceof Number n ? Optional.of(n.doubleValue()) : Optional.empty();
    }

    /**
     * Extracts a boolean value if possible.
     *
     * @param input input object
     * @return optional boolean
     */
    @Override
    public Optional<Boolean> getBooleanValue(Object input) {
        return input instanceof Boolean b ? Optional.of(b) : Optional.empty();
    }

    /**
     * Extracts a list if the input is a list.
     *
     * @param input input object
     * @return optional list
     */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<Object>> getList(Object input) {
        return (input instanceof List<?>) ? Optional.of((List<Object>) input) : Optional.empty();
    }

    /**
     * Extracts a map if the input is a map.
     *
     * @param input input object
     * @return optional map
     */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<Map<Object, Object>> getMap(Object input) {
        return (input instanceof Map<?, ?>) ? Optional.of((Map<Object, Object>) input) : Optional.empty();
    }

    /**
     * Returns map keys as strings if input is a map.
     *
     * @param input input object
     * @return iterable of keys
     */
    @Override
    public Optional<Iterable<String>> getKeys(Object input) {
        if (input instanceof Map<?, ?> m) {
            return Optional.of(m.keySet()
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        }
        return Optional.empty();
    }

    /**
     * Returns the size of a collection or map.
     *
     * @param input input object
     * @return optional size
     */
    @Override
    public OptionalInt size(Object input) {
        if (input instanceof Collection<?> c) return OptionalInt.of(c.size());
        if (input instanceof Map<?, ?> m) return OptionalInt.of(m.size());
        return OptionalInt.empty();
    }

    /**
     * Creates a deep copy of a YAML structure.
     *
     * @param input input object
     * @return deep-copied structure
     */
    @Override
    public Object copy(Object input) {
        return deepCopyYaml(input);
    }

    /**
     * Returns the YAML empty value representation.
     *
     * @return null
     */
    @Override
    public Object empty() {
        return null;
    }

    /**
     * Parses YAML text into a normalized object graph.
     *
     * @param yamlText YAML string
     * @return parsed object structure
     */
    public Object parse(String yamlText) {
        return normalize(yaml.load(yamlText));
    }

    /**
     * Parses YAML from an input stream.
     *
     * @param in input stream
     * @return parsed object structure
     */
    public Object parse(InputStream in) {
        return normalize(yaml.load(in));
    }

    /**
     * Serializes a Java object into YAML.
     *
     * @param obj object
     * @return YAML string
     */
    public static String dump(Object obj) {
        return yaml.dump(obj);
    }

    /**
     * Normalizes SnakeYAML output into consistent Map/List structures.
     *
     * @param obj raw object
     * @return normalized structure
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
        }
        return obj;
    }

    /**
     * Performs a deep copy of a YAML structure.
     *
     * @param obj input object
     * @return copied structure
     */
    private Object deepCopyYaml(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            Map<Object, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                copy.put(deepCopyYaml(e.getKey()), deepCopyYaml(e.getValue()));
            }
            return copy;
        } else if (obj instanceof List<?> list) {
            List<Object> copy = new ArrayList<>();
            for (Object o : list) {
                copy.add(deepCopyYaml(o));
            }
            return copy;
        }
        return obj;
    }
}