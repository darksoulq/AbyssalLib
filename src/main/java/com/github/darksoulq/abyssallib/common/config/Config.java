package com.github.darksoulq.abyssallib.common.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A wrapper around {@link YamlConfiguration} that provides
 * typed access to configuration values with default values
 * and support for inline comments.
 */
public class Config {

    private final File file;
    private final YamlConfiguration yaml;
    private final Map<String, List<String>> comments = new HashMap<>();

    /**
     * Constructs a configuration file under a plugin-specific subfolder.
     *
     * @param pluginId  the plugin identifier (used as a folder name)
     * @param name      the configuration file name (without .yml)
     * @param subfolder optional subfolder inside the pluginId folder
     */
    public Config(String pluginId, String name, String subfolder) {
        this.file = Path.of("config", pluginId, subfolder, name + ".yml").toFile();
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Constructs a configuration file under the main plugin folder.
     *
     * @param pluginId the plugin identifier (used as a folder name)
     * @param name     the configuration file name (without .yml)
     */
    public Config(String pluginId, String name) {
        this.file = Path.of("config", pluginId, name + ".yml").toFile();
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Retrieves a typed value at the given path, initializing it with a default if not present.
     *
     * @param path         the configuration path
     * @param defaultValue the default value if the path is missing
     * @param <T>          the value type
     * @return a {@link Value} wrapper for accessing and modifying the configuration
     */
    public <T> Value<T> value(String path, T defaultValue) {
        Value<T> val = new Value<>(path, defaultValue);
        if (!yaml.contains(path)) val.set(defaultValue);
        return val;
    }

    /**
     * Adds one or more comment lines to a configuration path.
     * Comments are written above the path during {@link #save()}.
     *
     * @param path         the configuration path
     * @param commentLines the comment lines to add
     */
    public void addComment(String path, String... commentLines) {
        comments.put(path, Arrays.asList(commentLines));
    }

    /**
     * Saves the configuration file to disk and writes registered comments.
     */
    public void save() {
        try {
            yaml.save(file);
            writeComments();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save configuration file: " + file, e);
        }
    }

    /**
     * Reloads the configuration file from disk, preserving default values.
     */
    public void reload() {
        yaml.options().copyDefaults(true);
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Failed to reload configuration file: " + file, e);
        }
    }

    /**
     * Writes registered comments above configuration paths in the YAML file.
     *
     * @throws IOException if writing fails
     */
    private void writeComments() throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        List<String> newLines = new ArrayList<>();
        Deque<String> pathStack = new ArrayDeque<>();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("#") || !trimmed.contains(":")) {
                newLines.add(line);
                continue;
            }

            int indent = countLeadingSpaces(line) / 2;
            while (pathStack.size() > indent) pathStack.removeLast();

            String key = trimmed.split(":")[0];
            pathStack.addLast(key);
            String fullPath = String.join(".", pathStack);

            if (comments.containsKey(fullPath)) {
                for (String commentLine : comments.get(fullPath)) {
                    newLines.add("  ".repeat(indent) + "# " + commentLine);
                }
            }

            newLines.add(line);
        }

        Files.write(file.toPath(), newLines);
    }

    /**
     * Counts the leading spaces in a string.
     *
     * @param line the line to analyze
     * @return the number of leading spaces
     */
    private int countLeadingSpaces(String line) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == ' ') count++;
        return count;
    }

    /**
     * Represents a typed configuration value at a specific path.
     * Supports default values, reading, writing, and adding comments.
     *
     * @param <T> the value type
     */
    public class Value<T> {

        private final String path;
        private final T defaultValue;

        /**
         * Creates a new typed value wrapper.
         *
         * @param path         the configuration path
         * @param defaultValue the default value if missing
         */
        public Value(String path, T defaultValue) {
            this.path = path;
            this.defaultValue = defaultValue;
        }

        /**
         * Gets the current value from the configuration, converting sections and lists recursively.
         *
         * @return the current value
         */
        @SuppressWarnings("unchecked")
        public T get() {
            return (T) readRaw(yaml.get(path, defaultValue));
        }

        /**
         * Sets the value in the configuration.
         *
         * @param value the value to set
         */
        public void set(T value) {
            yaml.set(path, value);
        }

        /**
         * Registers one or more comments for this value.
         *
         * @param comments the comment lines
         * @return this {@link Value} for chaining
         */
        public Value<T> withComment(String... comments) {
            addComment(path, comments);
            return this;
        }

        /**
         * Recursively converts configuration sections and lists to maps and lists of raw objects.
         *
         * @param obj the object to convert
         * @return the converted raw object
         */
        private Object readRaw(Object obj) {
            if (obj instanceof ConfigurationSection section) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (String key : section.getKeys(false)) {
                    map.put(key, readRaw(section.get(key)));
                }
                return map;
            } else if (obj instanceof List<?> list) {
                List<Object> newList = new ArrayList<>();
                for (Object item : list) {
                    newList.add(readRaw(item));
                }
                return newList;
            } else {
                return obj;
            }
        }
    }
}
