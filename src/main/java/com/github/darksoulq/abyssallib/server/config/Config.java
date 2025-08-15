package com.github.darksoulq.abyssallib.server.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Config is a wrapper around Bukkit's {@link YamlConfiguration} that provides:
 * <ul>
 *     <li>Default value management</li>
 *     <li>Easy save/reload functionality</li>
 *     <li>Optional type-safe value wrappers for instance-based access</li>
 * </ul>
 */
public class Config {
    private final Map<String, Object> defaults = new HashMap<>();
    private YamlConfiguration config;
    private final Path filePath;

    /**
     * Constructs a new Config instance for a specific file.
     *
     * @param path      The file name, e.g., "config.yml"
     * @param id        The subfolder under "config" (e.g., plugin name)
     * @param subFolder Additional subfolder path within the config folder
     */
    public Config(String path, String id, String subFolder) {
        this.filePath = Path.of("config", id, subFolder, path);
        File file = filePath.toFile();
        if (!file.exists()) {
            try {
                Files.createDirectories(filePath.getParent());
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Sets a default value for a configuration path.
     * Defaults are applied during {@link #save()} if the key is missing.
     *
     * @param path  The config key path
     * @param value The default value
     */
    public void setDefault(String path, Object value) {
        defaults.put(path, value);
    }

    /**
     * Sets a value in the configuration.
     *
     * @param path  The config key path
     * @param value The value to set
     */
    public void set(String path, Object value) {
        config.set(path, value);
    }

    /**
     * Retrieves a value from the configuration.
     * Falls back to the default value if the key is not present.
     *
     * @param path The config key path
     * @param type The expected type of the value
     * @param <T>  Type parameter
     * @return The value cast to the specified type, or null if missing
     */
    public <T> T get(String path, Class<T> type) {
        Object value = config.get(path, defaults.get(path));
        if (value == null) return null;
        return type.cast(value);
    }

    /**
     * Saves the configuration to disk, repopulating missing defaults.
     */
    public void save() {
        save(true);
    }

    /**
     * Saves the configuration to disk.
     *
     * @param populateDefaults If true, missing keys from defaults are added before saving
     */
    public void save(boolean populateDefaults) {
        if (populateDefaults) {
            for (Map.Entry<String, Object> entry : defaults.entrySet()) {
                if (!config.contains(entry.getKey())) {
                    config.set(entry.getKey(), entry.getValue());
                }
            }
        }
        try {
            config.save(filePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reloads the configuration from disk, discarding unsaved changes.
     */
    public void reload() {
        config = YamlConfiguration.loadConfiguration(filePath.toFile());
    }

    /**
     * Retrieves a value or sets and returns the provided default if missing.
     *
     * @param path         The config key path
     * @param defaultValue The default value to use if key is missing
     * @param <T>          Type parameter
     * @return The value in the configuration or the default
     */
    public <T> T getOrDefault(String path, T defaultValue) {
        if (!config.contains(path) && defaultValue != null) {
            config.set(path, defaultValue);
        }
        return (T) config.get(path, defaultValue);
    }

    /**
     * A convenient wrapper for a single config value that can be accessed and modified.
     * Automatically pulls defaults if the key is missing.
     *
     * @param <T> Type of the value
     */
    public static class Value<T> {
        private final Config config;
        private final String path;
        private final T defaultValue;

        /**
         * Constructs a new Value wrapper.
         *
         * @param config       The Config instance
         * @param path         The config key path
         * @param defaultValue Default value if key is missing
         */
        public Value(Config config, String path, T defaultValue) {
            this.config = config;
            this.path = path;
            this.defaultValue = defaultValue;
            config.setDefault(path, defaultValue);
        }

        /**
         * Retrieves the current value from the config.
         *
         * @return The current value
         */
        public T get() {
            return config.getOrDefault(path, defaultValue);
        }

        /**
         * Updates the value in the configuration.
         *
         * @param value The new value
         */
        public void set(T value) {
            config.set(path, value);
        }
    }
}
