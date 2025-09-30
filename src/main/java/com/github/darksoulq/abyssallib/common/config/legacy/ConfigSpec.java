package com.github.darksoulq.abyssallib.common.config.legacy;

import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A flexible specification for defining and validating configuration values.
 * Supports primitive types, lists, and optional numeric range constraints.
 */
public class ConfigSpec {

    /**
     * Represents supported configuration value types.
     */
    public enum ConfigType {
        /** Integer type, with optional range validation. */
        INT,
        /** Float type, with optional range validation. */
        FLOAT,
        /** Boolean type (true/false). */
        BOOLEAN,
        /** String type. */
        STRING,
        /** List of integers. */
        LIST_INT,
        /** List of floats. */
        LIST_FLOAT,
        /** List of booleans. */
        LIST_BOOLEAN,
        /** List of strings. */
        LIST_STRING
    }

    /**
     * Represents a numeric range for validation.
     *
     * @param min Minimum allowed value.
     * @param max Maximum allowed value.
     */
    public record Range(Number min, Number max) {}

    /**
     * Represents the definition of a config key.
     *
     * @param type         The value type.
     * @param defaultValue The default value.
     * @param range        Optional numeric range constraint.
     */
    private record Definition(ConfigType type, Object defaultValue, Range range) {}

    private final Map<String, Definition> definitions = new HashMap<>();
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    /**
     * Constructs an empty configuration specification.
     */
    public ConfigSpec() {}

    /**
     * Constructs a configuration specification from a JSON input stream.
     * Default values are inferred from the JSON structure.
     *
     * @param defaultsFile Input stream containing JSON-formatted config defaults.
     */
    public ConfigSpec(InputStream defaultsFile) {
        loadDefaults(defaultsFile);
    }

    /**
     * Loads and parses default configuration values from a JSON file.
     *
     * @param inputStream Input stream of the JSON file.
     */
    private void loadDefaults(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            JsonElement json = TextUtil.GSON.fromJson(reader, JsonElement.class);
            if (json != null && json.isJsonObject()) {
                parseDefaults(json.getAsJsonObject(), "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursively parses a JSON object into default config values.
     *
     * @param object JSON object to parse.
     * @param path   Current base path (for nesting).
     */
    private void parseDefaults(JsonObject object, String path) {
        for (var entry : object.entrySet()) {
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
     * Infers the ConfigType from a parsed value.
     *
     * @param value Parsed value.
     * @return Inferred ConfigType.
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
     * Defines a configuration key without range validation.
     *
     * @param type         The value type.
     * @param path         Dot-separated key path.
     * @param defaultValue The default value.
     * @param <T>          Type of the value.
     */
    public <T> void define(ConfigType type, String path, T defaultValue) {
        define(type, path, defaultValue, null, null);
    }

    /**
     * Defines a configuration key with optional range validation (for numeric types).
     *
     * @param type         The value type.
     * @param path         Dot-separated key path.
     * @param defaultValue The default value.
     * @param min          Optional minimum value.
     * @param max          Optional maximum value.
     * @param <T>          Type of the value.
     */
    public <T> void define(ConfigType type, String path, T defaultValue, Number min, Number max) {
        Range range = (min != null && max != null && (type == ConfigType.INT || type == ConfigType.FLOAT))
                ? new Range(min, max) : null;
        Object safeDefault = coerceValue(type, defaultValue, range);
        definitions.put(path, new Definition(type, safeDefault, range));
        values.putIfAbsent(path, safeDefault);
    }

    /**
     * Gets a typed configuration value.
     *
     * @param type Expected ConfigType.
     * @param path Dot-separated key path.
     * @param <T>  Type of the value.
     * @return The value or null if undefined or type mismatch.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(ConfigType type, String path) {
        Object val = values.get(path);
        Definition def = definitions.get(path);
        if (def == null || val == null || def.type != type) return null;
        return (T) val;
    }

    /**
     * Sets a configuration value.
     *
     * @param type  Expected ConfigType.
     * @param path  Dot-separated key path.
     * @param value New value to set.
     */
    public void set(ConfigType type, String path, Object value) {
        Definition def = definitions.get(path);
        if (def == null || def.type != type) return;

        Object coerced = coerceValue(type, value, def.range);
        if (coerced != null) {
            values.put(path, coerced);
        }
    }

    /**
     * Gets the raw untyped value at a given path.
     *
     * @param path Dot-separated key path.
     * @return The raw value or null if not present.
     */
    public Object get(String path) {
        return values.get(path);
    }

    /**
     * Returns a copy of all current configuration values.
     *
     * @return Map of key paths to values.
     */
    public Map<String, Object> getAllValues() {
        return values;
    }

    /**
     * Returns all default values defined in the spec.
     *
     * @return Map of key paths to default values.
     */
    public Map<String, Object> getAllDefaults() {
        Map<String, Object> out = new HashMap<>();
        for (var entry : definitions.entrySet()) {
            out.put(entry.getKey(), entry.getValue().defaultValue);
        }
        return out;
    }

    /**
     * Gets the type defined for a given key path.
     *
     * @param path Dot-separated key path.
     * @return ConfigType or null if not defined.
     */
    public ConfigType getDefinitionType(String path) {
        Definition def = definitions.get(path);
        return def != null ? def.type : null;
    }

    /**
     * Gets the numeric range for a given path if defined.
     *
     * @param path Dot-separated key path.
     * @return Range object or null if not applicable.
     */
    public Range getRange(String path) {
        Definition def = definitions.get(path);
        return def != null ? def.range : null;
    }

    /**
     * Attempts to coerce a value to the correct type, applying range validation if applicable.
     *
     * @param type  The expected ConfigType.
     * @param value The value to coerce.
     * @param range Optional range constraint.
     * @return Coerced and validated value or null on failure.
     */
    private Object coerceValue(ConfigType type, Object value, Range range) {
        try {
            return switch (type) {
                case INT -> {
                    int i = (value instanceof Number) ? ((Number) value).intValue() : Integer.parseInt(value.toString());
                    if (range != null && (i < range.min.intValue() || i > range.max.intValue())) yield range.min.intValue();
                    yield i;
                }
                case FLOAT -> {
                    float f = (value instanceof Number) ? ((Number) value).floatValue() : Float.parseFloat(value.toString());
                    if (range != null && (f < range.min.floatValue() || f > range.max.floatValue())) yield range.min.floatValue();
                    yield f;
                }
                case BOOLEAN -> (value instanceof Boolean) ? value : Boolean.parseBoolean(value.toString());
                case STRING -> value.toString();
                case LIST_INT -> ((List<?>) value).stream().map(o -> Integer.parseInt(o.toString())).toList();
                case LIST_FLOAT -> ((List<?>) value).stream().map(o -> Float.parseFloat(o.toString())).toList();
                case LIST_BOOLEAN -> ((List<?>) value).stream().map(o -> Boolean.parseBoolean(o.toString())).toList();
                case LIST_STRING -> ((List<?>) value).stream().map(Object::toString).toList();
            };
        } catch (Exception e) {
            return null;
        }
    }

    /** @return Integer value at the specified path, or null if not found. */
    public Integer getInt(String path) {
        return get(ConfigType.INT, path);
    }

    /** @return Float value at the specified path, or null if not found. */
    public Float getFloat(String path) {
        return get(ConfigType.FLOAT, path);
    }

    /** @return Boolean value at the specified path, or null if not found. */
    public Boolean getBoolean(String path) {
        return get(ConfigType.BOOLEAN, path);
    }

    /** @return String value at the specified path, or null if not found. */
    public String getString(String path) {
        return get(ConfigType.STRING, path);
    }

    /** @return List of integers at the specified path, or null if not found. */
    public List<Integer> getListInt(String path) {
        return get(ConfigType.LIST_INT, path);
    }

    /** @return List of floats at the specified path, or null if not found. */
    public List<Float> getListFloat(String path) {
        return get(ConfigType.LIST_FLOAT, path);
    }

    /** @return List of booleans at the specified path, or null if not found. */
    public List<Boolean> getListBoolean(String path) {
        return get(ConfigType.LIST_BOOLEAN, path);
    }

    /** @return List of strings at the specified path, or null if not found. */
    public List<String> getListString(String path) {
        return get(ConfigType.LIST_STRING, path);
    }

    /**
     * Returns a string representation of the current configuration values.
     *
     * @return Stringified config values.
     */
    @Override
    public String toString() {
        return values.toString();
    }
}
