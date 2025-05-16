package com.github.darksoulq.abyssallib.config;

import com.github.darksoulq.abyssallib.util.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a specification for configuration values. This class allows defining
 * various typed configuration values, optionally with a set of allowed values.
 * It supports automatic coercion, validation, and default fallback behavior.
 */
public class ConfigSpec {
    /**
     * Enum representing the supported types for configuration values.
     */
    public enum ConfigType {
        INT, FLOAT, BOOLEAN, STRING,
        LIST_INT, LIST_FLOAT, LIST_BOOLEAN, LIST_STRING,
        RESTRICTED_INT, RESTRICTED_FLOAT, RESTRICTED_BOOLEAN, RESTRICTED_STRING
    }

    private record Definition(ConfigType type, Object defaultValue, List<Object> allowedValues) {}

    private final Map<String, Definition> definitions = new HashMap<>();
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    /**
     * Constructs an empty config specification.
     */
    public ConfigSpec() {}
    /**
     * Constructs a config specification and loads default values from a JSON input stream.
     *
     * @param defaultsFile The input stream containing JSON defaults.
     */
    public ConfigSpec(InputStream defaultsFile) {
        loadDefaults(defaultsFile);
    }

    /**
     * Loads default values from a JSON input stream.
     *
     * @param inputStream The input stream containing JSON.
     */
    private void loadDefaults(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            JsonElement jsonElement = FileUtils.GSON.fromJson(reader, JsonElement.class);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                parseDefaults(jsonElement.getAsJsonObject(), "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseDefaults(JsonObject jsonObject, String path) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String fullPath = path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();
            JsonElement value = entry.getValue();

            if (value.isJsonObject()) {
                parseDefaults(value.getAsJsonObject(), fullPath);
            } else {
                Object parsed = ConfigParser.parseValue(value);
                ConfigType type = inferConfigType(parsed);
                define(type, fullPath, parsed);
            }
        }
    }

    /**
     * Infers the ConfigType from a parsed Java object.
     *
     * @param value The parsed value.
     * @return The most appropriate ConfigType.
     */
    private ConfigType inferConfigType(Object value) {
        if (value instanceof Integer) return ConfigType.INT;
        if (value instanceof Float || value instanceof Double) return ConfigType.FLOAT;
        if (value instanceof Boolean) return ConfigType.BOOLEAN;
        if (value instanceof String) return ConfigType.STRING;
        if (value instanceof List<?> list) {
            if (list.isEmpty()) return ConfigType.LIST_STRING;
            Object first = list.getFirst();
            if (first instanceof Integer) return ConfigType.LIST_INT;
            if (first instanceof Float || first instanceof Double) return ConfigType.LIST_FLOAT;
            if (first instanceof Boolean) return ConfigType.LIST_BOOLEAN;
            return ConfigType.LIST_STRING;
        }
        return ConfigType.STRING;
    }


    /**
     * Defines a new configuration value.
     *
     * @param type         The type of the config value.
     * @param path         The dot-separated path used to identify this value.
     * @param defaultValue The default value to use if none is present.
     * @param <T>          The value type.
     */
    public <T> void define(ConfigType type, String path, T defaultValue) {
        define(type, path, defaultValue, null);
    }

    /**
     * Defines a new configuration value with a set of allowed values.
     *
     * @param type          The type of the config value.
     * @param path          The dot-separated path used to identify this value.
     * @param defaultValue  The default value to use if none is present.
     * @param allowedValues A list of allowed values (only applies to RESTRICTED_* types).
     * @param <T>           The value type.
     */
    public <T> void define(ConfigType type, String path, T defaultValue, List<T> allowedValues) {
        List<Object> allowed = allowedValues != null ? new ArrayList<>(allowedValues) : null;
        Object safeDefault = coerceValue(type, defaultValue, allowed);
        definitions.put(path, new Definition(type, safeDefault, allowed));
        values.putIfAbsent(path, safeDefault);
    }

    /**
     * Retrieves a typed config value by path.
     *
     * @param type The expected type.
     * @param path The dot-separated path.
     * @param <T>  The expected type.
     * @return The config value or {@code null} if not found or type mismatch.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(ConfigType type, String path) {
        Object val = values.get(path);
        Definition def = definitions.get(path);

        if (def == null || val == null) return null;

        if (!type.equals(def.type)) return null;
        if (def.type.name().startsWith("RESTRICTED") && def.allowedValues != null) {
            if (!def.allowedValues.contains(val)) return (T) def.defaultValue;
        }

        return (T) val;
    }

    /**
     * Sets a value at the specified path, coercing and validating it according to its definition.
     *
     * @param type  The expected type.
     * @param path  The dot-separated path.
     * @param value The new value.
     */
    public void set(ConfigType type, String path, Object value) {
        Definition def = definitions.get(path);
        if (def == null || !def.type.equals(type)) return;

        Object coerced = coerceValue(type, value, def.allowedValues);
        if (coerced != null) {
            values.put(path, coerced);
        }
    }

    /**
     * Gets the raw untyped value at the specified path.
     *
     * @param path The dot-separated path.
     * @return The value or {@code null}.
     */
    public Object get(String path) {
        return values.get(path);
    }

    /**
     * Returns all current values (path → value).
     */
    public Map<String, Object> getAllValues() {
        return values;
    }

    /**
     * Returns all default values from definitions.
     */
    public Map<String, Object> getAllDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        for (Map.Entry<String, Definition> entry : definitions.entrySet()) {
            defaults.put(entry.getKey(), entry.getValue().defaultValue);
        }
        return defaults;
    }

    /** Convenience typed getters **/

    public int getInt(String path) {
        Number n = get(ConfigType.INT, path);
        return n != null ? n.intValue() : 0;
    }

    public float getFloat(String path) {
        Number n = get(ConfigType.FLOAT, path);
        return n != null ? n.floatValue() : 0.0f;
    }

    public double getDouble(String path) {
        Number n = get(ConfigType.FLOAT, path);
        return n != null ? n.doubleValue() : 0.0;
    }

    public boolean getBoolean(String path) {
        Boolean b = get(ConfigType.BOOLEAN, path);
        return b != null && b;
    }

    public String getString(String path) {
        String s = get(ConfigType.STRING, path);
        return s != null ? s : "";
    }

    public List<Integer> getIntList(String path) {
        return get(ConfigType.LIST_INT, path);
    }

    public List<Float> getFloatList(String path) {
        return get(ConfigType.LIST_FLOAT, path);
    }

    public List<Boolean> getBooleanList(String path) {
        return get(ConfigType.LIST_BOOLEAN, path);
    }

    public List<String> getStringList(String path) {
        return get(ConfigType.LIST_STRING, path);
    }

    /**
     * Attempts to coerce a given value into the specified {@link ConfigType}.
     * Falls back to the first allowed value (if restricted) or null.
     */
    private Object coerceValue(ConfigType type, Object value, List<Object> allowed) {
        try {
            switch (type) {
                case INT, RESTRICTED_INT -> {
                    int i = (value instanceof Number) ? ((Number) value).intValue() : Integer.parseInt(value.toString());
                    if (allowed == null || allowed.contains(i)) return i;
                    return allowed != null ? allowed.getFirst() : null;
                }
                case FLOAT, RESTRICTED_FLOAT -> {
                    float f = (value instanceof Number) ? ((Number) value).floatValue() : Float.parseFloat(value.toString());
                    if (allowed == null || allowed.contains(f)) return f;
                    return allowed != null ? allowed.getFirst() : null;
                }
                case BOOLEAN, RESTRICTED_BOOLEAN -> {
                    boolean b = (value instanceof Boolean) ? (Boolean) value : Boolean.parseBoolean(value.toString());
                    if (allowed == null || allowed.contains(b)) return b;
                    return allowed != null ? allowed.getFirst() : null;
                }
                case STRING, RESTRICTED_STRING -> {
                    String s = value.toString();
                    if (allowed == null || allowed.contains(s)) return s;
                    return allowed != null ? allowed.getFirst() : null;
                }
                case LIST_INT -> {
                    if (value instanceof List<?>) {
                        List<Integer> list = new ArrayList<>();
                        for (Object o : (List<?>) value) list.add(Integer.parseInt(o.toString()));
                        return list;
                    }
                }
                case LIST_FLOAT -> {
                    if (value instanceof List<?>) {
                        List<Float> list = new ArrayList<>();
                        for (Object o : (List<?>) value) list.add(Float.parseFloat(o.toString()));
                        return list;
                    }
                }
                case LIST_BOOLEAN -> {
                    if (value instanceof List<?>) {
                        List<Boolean> list = new ArrayList<>();
                        for (Object o : (List<?>) value) list.add(Boolean.parseBoolean(o.toString()));
                        return list;
                    }
                }
                case LIST_STRING -> {
                    if (value instanceof List<?>) {
                        List<String> list = new ArrayList<>();
                        for (Object o : (List<?>) value) list.add(o.toString());
                        return list;
                    }
                }
            }
        } catch (Exception e) {
            return allowed != null && !allowed.isEmpty() ? allowed.getFirst() : null;
        }
        return null;
    }

    /**
     * Returns the {@link ConfigType} of a defined path.
     *
     * @param path The path.
     * @return The type, or {@code null} if not defined.
     */
    public ConfigType getDefinitionType(String path) {
        Definition def = definitions.get(path);
        return def != null ? def.type : null;
    }

    /**
     * Returns the list of allowed values for a restricted path.
     *
     * @param path The path.
     * @return A list of allowed values, or {@code null}.
     */
    public List<Object> getAllowedValues(String path) {
        Definition def = definitions.get(path);
        return def != null ? def.allowedValues : null;
    }

    /**
     * Returns the string representation of current config values.
     */
    @Override
    public String toString() {
        return values.toString();
    }
}
