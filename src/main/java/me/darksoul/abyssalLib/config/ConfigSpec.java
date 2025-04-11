package me.darksoul.abyssalLib.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigSpec {
    private final Map<String, Object> defaults = new HashMap<>();
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public <T> void define(String path, T defaultValue) {
        defaults.put(path, defaultValue);
        values.putIfAbsent(path, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String path, Class<T> type) {
        Object val = values.get(path);
        return (val != null && type.isInstance(val)) ? (T) val : null;
    }

    public Object get(String path) {
        return values.get(path);
    }

    public int getInt(String path) {
        Number n = get(path, Number.class);
        return n != null ? n.intValue() : 0;
    }

    public boolean getBoolean(String path) {
        Boolean b = get(path, Boolean.class);
        return b != null ? b : false;
    }

    public double getDouble(String path) {
        Number n = get(path, Number.class);
        return n != null ? n.doubleValue() : 0.0;
    }

    public String getString(String path) {
        String s = get(path, String.class);
        return s != null ? s : "";
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path, Class<T> elementType) {
        Object obj = values.get(path);
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            for (Object item : list) {
                if (item != null && !elementType.isInstance(item)) {
                    return Collections.emptyList(); // type mismatch
                }
            }
            return (List<T>) list;
        }
        return Collections.emptyList();
    }


    public Map<String, Object> getAllValues() {
        return values;
    }

    public Map<String, Object> getAllDefaults() {
        return defaults;
    }

    public void set(String path, Object value) {
        values.put(path, value);
    }
}
