package com.github.darksoulq.abyssallib.config;

import com.github.darksoulq.abyssallib.util.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a structured configuration specification that supports default values and runtime overrides.
 * Values are stored using dot-separated keys to support nesting.
 */
public class ConfigSpec {
    /**
     * A map holding the default values for configuration settings.
     * These are loaded from the default configuration file.
     */
    private final Map<String, Object> defaults = new HashMap<>();
    /**
     * A map holding the current values for configuration settings.
     * These values can be modified at runtime.
     */
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    /**
     * Creates a new config spec and loads default values from the given input stream.
     * This must be a JSON file located in the plugin resources.
     *
     * @param defaultsFile The input stream pointing to a JSON file containing default config values.
     */
    public ConfigSpec(InputStream defaultsFile) {
        loadDefaults(defaultsFile);
    }

    /**
     * Creates an empty config spec with no default values.
     * Values must be defined manually using {@link #define(String, Object)}.
     */
    public ConfigSpec() {
    }

    /**
     * Loads the default configuration values from the given input stream.
     * The configuration file is expected to be in JSON format.
     *
     * @param inputStream The input stream pointing to a JSON file containing default config values.
     */
    private void loadDefaults(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            JsonElement jsonElement = FileUtils.GSON.fromJson(reader, JsonElement.class);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                parseDefaults(jsonObject, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursively parses a JSON object and stores the default values in the defaults map.
     * Each value is stored with a dot-separated key to support nesting.
     *
     * @param jsonObject The JSON object to parse.
     * @param parentPath The current path being processed (used for nested values).
     */
    private void parseDefaults(JsonObject jsonObject, String parentPath) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String fullPath = parentPath.isEmpty() ? entry.getKey() : parentPath + "." + entry.getKey();
            JsonElement value = entry.getValue();
            if (value.isJsonObject()) {
                parseDefaults(value.getAsJsonObject(), fullPath);
            } else {
                defaults.put(fullPath, ConfigParser.parseValue(value));
            }
        }
    }

    /**
     * Defines a new configuration value with a default. If a value for this path is not set, the default will be used.
     *
     * @param path         The dot-separated key path.
     * @param defaultValue The default value to use if no value is present.
     * @param <T>          The type of the value.
     */
    public <T> void define(String path, T defaultValue) {
        defaults.put(path, defaultValue);
        values.putIfAbsent(path, defaultValue);
    }

    /**
     * Retrieves the value at the given path, cast to the specified type.
     *
     * @param path The dot-separated key path.
     * @param type The expected type of the value.
     * @param <T>  The type parameter.
     * @return The value, or {@code null} if it is not present or not of the correct type.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String path, Class<T> type) {
        Object val = values.get(path);
        return (val != null && type.isInstance(val)) ? (T) val : null;
    }

    /**
     * Retrieves the raw value at the given path.
     *
     * @param path The dot-separated key path.
     * @return The value, or {@code null} if not set.
     */
    public Object get(String path) {
        return values.get(path);
    }

    /**
     * Retrieves an integer value at the given path.
     *
     * @param path The dot-separated key path.
     * @return The integer value, or {@code 0} if not found or not a number.
     */
    public int getInt(String path) {
        Number n = get(path, Number.class);
        return n != null ? n.intValue() : 0;
    }

    /**
     * Retrieves a boolean value at the given path.
     *
     * @param path The dot-separated key path.
     * @return The boolean value, or {@code false} if not found or not a boolean.
     */
    public boolean getBoolean(String path) {
        Boolean b = get(path, Boolean.class);
        return b != null ? b : false;
    }

    /**
     * Retrieves a double value at the given path.
     *
     * @param path The dot-separated key path.
     * @return The double value, or {@code 0.0} if not found or not a number.
     */
    public double getDouble(String path) {
        Number n = get(path, Number.class);
        return n != null ? n.doubleValue() : 0.0;
    }

    /**
     * Retrieves a float value at the given path.
     *
     * @param path The dot-separated key path.
     * @return The float value, or {@code 0.0f} if not found or not a number.
     */
    public float getFloat(String path) {
        Number n = get(path, Number.class);
        return n != null ? n.floatValue() : 0.0f;
    }

    /**
     * Retrieves a string value at the given path.
     *
     * @param path The dot-separated key path.
     * @return The string value, or an empty string if not found or not a string.
     */
    public String getString(String path) {
        String s = get(path, String.class);
        return s != null ? s : "";
    }

    /**
     * Retrieves a list of values at the given path, ensuring all elements match the specified type.
     *
     * @param path        The dot-separated key path.
     * @param elementType The expected class of each element in the list.
     * @param <T>         The type of the elements.
     * @return A list of values, or an empty list if not found or if type mismatch occurs.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path, Class<T> elementType) {
        Object obj = values.get(path);
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            for (Object item : list) {
                if (item != null && !elementType.isInstance(item)) {
                    return Collections.emptyList();
                }
            }
            return (List<T>) list;
        }
        return Collections.emptyList();
    }

    /**
     * Returns all current configuration values.
     *
     * @return A map of key-value pairs representing the current config state.
     */
    public Map<String, Object> getAllValues() {
        return values;
    }

    /**
     * Returns all defined default values.
     *
     * @return A map of key-value pairs representing all defaults.
     */
    public Map<String, Object> getAllDefaults() {
        return defaults;
    }

    /**
     * Sets the value for the given path. This overrides any default or previous value.
     *
     * @param path  The dot-separated key path.
     * @param value The value to set.
     */
    public void set(String path, Object value) {
        values.put(path, value);
    }

    /**
     * Returns a string representation of all current values in the config.
     *
     * @return A stringified version of the current config values.
     */
    @Override
    public String toString() {
        return values.toString();
    }
}
