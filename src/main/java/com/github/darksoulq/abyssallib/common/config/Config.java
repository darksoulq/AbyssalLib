package com.github.darksoulq.abyssallib.common.config;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A wrapper for Bukkit's {@link YamlConfiguration} that provides a fluent API,
 * support for {@link Codec} serialization, and manual comment injection.
 * <p>
 * This class allows for easy management of configuration files located within
 * a plugin's data folder.
 */
public class Config {

    /** The physical file on the disk. */
    private final File file;
    /** The underlying Bukkit YAML configuration instance. */
    private final YamlConfiguration yaml;
    /** A map storing comments associated with specific configuration paths. */
    private final Map<String, List<String>> comments = new HashMap<>();

    /**
     * Constructs a new Config instance located in a subfolder.
     *
     * @param pluginId  The ID/name of the plugin (used for the root folder).
     * @param name      The name of the YAML file (without extension).
     * @param subfolder The subfolder within the plugin's config directory.
     */
    public Config(String pluginId, String name, String subfolder) {
        this.file = Path.of("config", pluginId, subfolder, name + ".yml").toFile();
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Constructs a new Config instance in the plugin's root config folder.
     *
     * @param pluginId The ID/name of the plugin.
     * @param name     The name of the YAML file (without extension).
     */
    public Config(String pluginId, String name) {
        this.file = Path.of("config", pluginId, name + ".yml").toFile();
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Defines a configuration value with a default fallback.
     *
     * @param <T>          The type of the value.
     * @param path         The YAML path (e.g., "settings.enabled").
     * @param defaultValue The value to use and save if the path does not exist.
     * @return A {@link Value} object to interact with this configuration node.
     */
    public <T> Value<T> value(String path, T defaultValue) {
        Value<T> val = new Value<>(path, defaultValue, null);
        if (!yaml.contains(path)) val.set(defaultValue);
        return val;
    }

    /**
     * Defines a configuration value that requires a {@link Codec} for serialization.
     *
     * @param <T>          The type of the value.
     * @param path         The YAML path.
     * @param defaultValue The default value fallback.
     * @param codec        The codec used to encode/decode the value.
     * @return A {@link Value} object.
     */
    public <T> Value<T> value(String path, T defaultValue, Codec<T> codec) {
        Value<T> val = new Value<>(path, defaultValue, codec);
        if (!yaml.contains(path)) val.set(defaultValue);
        return val;
    }

    /**
     * Associates one or more comment lines with a configuration path.
     *
     * @param path         The configuration path to comment.
     * @param commentLines The lines of text to add as comments.
     */
    public void addComment(String path, String... commentLines) {
        comments.put(path, Arrays.asList(commentLines));
    }

    /**
     * Saves the current configuration state to the disk and injects defined comments.
     *
     * @throws RuntimeException If an {@link IOException} occurs during saving.
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
     * Reloads the configuration from the disk.
     *
     * @throws RuntimeException If the file is invalid or inaccessible.
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
     * Internal method to manually parse the saved file and inject comments above keys.
     * This compensates for standard Bukkit YAML not supporting per-key comments in older versions.
     *
     * @throws IOException If the file cannot be read or written.
     */
    private void writeComments() throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        List<String> newLines = new ArrayList<>();
        Deque<String> pathStack = new ArrayDeque<>();

        for (String line : lines) {
            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.startsWith("#") || !trimmed.contains(":")) {
                newLines.add(line);
                continue;
            }

            int indent = countLeadingSpaces(line) / 2;
            while (pathStack.size() > indent) pathStack.removeLast();

            String key = trimmed.split(":", 2)[0].trim();
            if (key.startsWith("-")) {
                newLines.add(line);
                continue;
            }
            if (key.startsWith("'") || key.startsWith("\"")) {
                key = key.substring(1, key.length() - 1);
            }
            pathStack.addLast(key);
            String fullPath = String.join(".", pathStack);

            boolean hasComments = comments.containsKey(fullPath);
            List<String> commentLines = hasComments ? comments.get(fullPath) : Collections.emptyList();

            boolean alreadyPresent = false;
            if (hasComments) {
                alreadyPresent = true;
                for (int j = 0; j < commentLines.size(); j++) {
                    int checkLineIndex = newLines.size() - commentLines.size() + j;
                    if (checkLineIndex < 0 || !newLines.get(checkLineIndex).trim().equals("# " + commentLines.get(j))) {
                        alreadyPresent = false;
                        break;
                    }
                }
            }

            int insertionIndex = newLines.size();
            if (alreadyPresent) {
                insertionIndex -= commentLines.size();
            }

            if (insertionIndex > 0) {
                String lineBefore = newLines.get(insertionIndex - 1).trim();
                if (!lineBefore.isEmpty() && !lineBefore.endsWith(":") && !lineBefore.startsWith("#")) {
                    if (indent == 0 || hasComments) {
                        newLines.add(insertionIndex, "");
                    }
                }
            }

            if (hasComments && !alreadyPresent) {
                for (String commentLine : commentLines) {
                    newLines.add("  ".repeat(indent) + "# " + commentLine);
                }
            }

            newLines.add(line);
        }

        while (!newLines.isEmpty() && newLines.get(newLines.size() - 1).trim().isEmpty()) {
            newLines.remove(newLines.size() - 1);
        }

        Files.write(file.toPath(), newLines);
    }

    /**
     * Counts the number of leading spaces in a string to determine YAML indentation.
     *
     * @param line The string to check.
     * @return The number of spaces.
     */
    private int countLeadingSpaces(String line) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == ' ') count++;
        return count;
    }

    /**
     * Represents a specific value within the configuration.
     * Provides methods to get and set data with optional {@link Codec} support.
     *
     * @param <T> The type of the value.
     */
    public class Value<T> {

        /** The configuration path for this value. */
        private final String path;
        /** The default value to return if none is found. */
        private final T defaultValue;
        /** The optional codec used for complex object mapping. */
        private final Codec<T> codec;

        /**
         * Constructs a new Config Value.
         *
         * @param path         The YAML path.
         * @param defaultValue The fallback value.
         * @param codec        The codec (can be null for primitives).
         */
        public Value(String path, T defaultValue, Codec<T> codec) {
            this.path = path;
            this.defaultValue = defaultValue;
            this.codec = codec;
        }

        /**
         * Retrieves the value from the configuration.
         *
         * @return The stored value, or the default if not present.
         * @throws RuntimeException If a {@link Codec.CodecException} occurs during decoding.
         */
        @SuppressWarnings("unchecked")
        public T get() {
            try {
                return (T) readRaw(yaml.get(path, defaultValue));
            } catch (Codec.CodecException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Sets the value in the configuration. If a codec is present, it will be used
         * to encode the value into a YAML-compatible format.
         *
         * @param value The value to store.
         * @throws RuntimeException If a {@link Codec.CodecException} occurs during encoding.
         */
        public void set(T value) {
            if (codec != null) {
                try {
                    yaml.set(path, codec.encode(YamlOps.INSTANCE, value));
                    return;
                } catch (Codec.CodecException e) {
                    throw new RuntimeException(e);
                }
            }
            yaml.set(path, value);
        }

        /**
         * Adds a comment to this specific value's path.
         *
         * @param comments The lines of the comment.
         * @return This {@link Value} instance for chaining.
         */
        public Value<T> withComment(String... comments) {
            addComment(path, comments);
            return this;
        }

        /**
         * Recursively processes configuration objects to ensure they are compatible
         * with the codec system and Java collection types.
         *
         * @param obj The raw object from the YAML configuration.
         * @return The processed object (Map, List, or decoded object).
         * @throws Codec.CodecException If decoding fails.
         */
        private Object readRaw(Object obj) throws Codec.CodecException {
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
                if (codec != null) {
                    return codec.decode(YamlOps.INSTANCE, obj);
                }
                return obj;
            }
        }
    }
}