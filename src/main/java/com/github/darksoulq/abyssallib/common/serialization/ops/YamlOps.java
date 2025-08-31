package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

public class YamlOps extends DynamicOps<Object> {

    public static final YamlOps INSTANCE = new YamlOps();

    private final Yaml yaml = new Yaml();

    private YamlOps() {}

    @Override
    public Object createString(String value) {
        return value;
    }

    @Override
    public Object createInt(int value) {
        return value;
    }

    @Override
    public Object createLong(long value) {
        return value;
    }

    @Override
    public Object createFloat(float value) {
        return value;
    }

    @Override
    public Object createDouble(double value) {
        return value;
    }

    @Override
    public Object createBoolean(boolean value) {
        return value;
    }

    @Override
    public Object createList(List<Object> elements) {
        return new ArrayList<>(elements);
    }

    @Override
    public Object createMap(Map<Object, Object> map) {
        return new LinkedHashMap<>(map);
    }

    @Override
    public Optional<String> getStringValue(Object input) {
        return input instanceof String s ? Optional.of(s) : Optional.empty();
    }

    @Override
    public Optional<Integer> getIntValue(Object input) {
        return input instanceof Integer i ? Optional.of(i) : Optional.empty();
    }

    @Override
    public Optional<Long> getLongValue(Object input) {
        return input instanceof Long l ? Optional.of(l) : Optional.empty();
    }

    @Override
    public Optional<Float> getFloatValue(Object input) {
        return input instanceof Float f ? Optional.of(f) : Optional.empty();
    }

    @Override
    public Optional<Double> getDoubleValue(Object input) {
        return input instanceof Double d ? Optional.of(d) : Optional.empty();
    }

    @Override
    public Optional<Boolean> getBooleanValue(Object input) {
        return input instanceof Boolean b ? Optional.of(b) : Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<Object>> getList(Object input) {
        return (input instanceof List<?>) ? Optional.of((List<Object>) input) : Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Map<Object, Object>> getMap(Object input) {
        return (input instanceof Map<?, ?>) ? Optional.of((Map<Object, Object>) input) : Optional.empty();
    }

    @Override
    public Object empty() {
        return null;
    }

    public Object parse(String yamlText) {
        return normalize(yaml.load(yamlText));
    }

    public Object parse(InputStream in) {
        return normalize(yaml.load(in));
    }

    public String dump(Object obj) {
        return yaml.dump(obj);
    }

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
