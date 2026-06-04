package com.github.darksoulq.abyssallib.common.config;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.fixer.DataFixer;
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

    /**
     * The physical file on the disk.
     */
    private final File file;
    /**
     * The underlying Bukkit YAML configuration instance.
     */
    private final YamlConfiguration yaml;
    /**
     * A map storing comments associated with specific configuration paths.
     */
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
     * Initializes a migration chain for this specific configuration file.
     *
     * @param targetVersion The final schema version this config should reach.
     * @return A {@link MigrationChain} to fluently define upgrade steps.
     */
    public MigrationChain schema(int targetVersion) {
        return new MigrationChain(targetVersion);
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
     * Recursively extracts a Bukkit {@link ConfigurationSection} into a standard Java {@link Map}.
     * This prepares the configuration data for processing by format-agnostic DataFixers.
     *
     * @param section The Bukkit configuration section to extract.
     * @return A normalized Map representation of the section.
     */
    private Map<String, Object> extractMap(ConfigurationSection section) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            Object obj = section.get(key);
            if (obj instanceof ConfigurationSection child) {
                map.put(key, extractMap(child));
            } else if (obj instanceof List<?> list) {
                map.put(key, normalizeList(list));
            } else {
                map.put(key, obj);
            }
        }
        return map;
    }

    /**
     * Recursively normalizes a list from the Bukkit configuration, converting any
     * nested {@link ConfigurationSection}s or Maps into standardized String-keyed Maps.
     *
     * @param list The raw list from the configuration.
     * @return A newly formatted list containing normalized elements.
     */
    private List<Object> normalizeList(List<?> list) {
        List<Object> newList = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof ConfigurationSection child) {
                newList.add(extractMap(child));
            } else if (item instanceof Map<?, ?> map) {
                Map<String, Object> stringMap = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    stringMap.put(entry.getKey().toString(), entry.getValue());
                }
                newList.add(stringMap);
            } else if (item instanceof List<?> nested) {
                newList.add(normalizeList(nested));
            } else {
                newList.add(item);
            }
        }
        return newList;
    }

    /**
     * Applies a standard Java {@link Map} back into a Bukkit {@link ConfigurationSection},
     * wiping existing keys at the current level and recursively building nested sections.
     *
     * @param section The Bukkit section to populate.
     * @param map     The map containing the upgraded data.
     */
    private void applyMap(ConfigurationSection section, Map<?, ?> map) {
        for (String key : section.getKeys(false)) {
            section.set(key, null);
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map<?, ?> childMap) {
                ConfigurationSection child = section.createSection(entry.getKey().toString());
                applyMap(child, childMap);
            } else {
                section.set(entry.getKey().toString(), entry.getValue());
            }
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

        while (!newLines.isEmpty() && newLines.getLast().trim().isEmpty()) {
            newLines.removeLast();
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
     * A fluent builder for defining data migrations specific to this configuration file.
     */
    public class MigrationChain {
        /**
         * The target version to upgrade the configuration to.
         */
        private final int targetVersion;
        /**
         * A sorted map of registered migration steps ensuring sequential execution.
         */
        private final TreeMap<Integer, DataFixer> steps = new TreeMap<>();

        /**
         * Constructs a new migration chain.
         *
         * @param targetVersion The final schema version this config should reach.
         */
        private MigrationChain(int targetVersion) {
            this.targetVersion = targetVersion;
        }

        /**
         * Registers a data fixer to transition the config from a specific version.
         *
         * @param fromVersion The version this fixer applies to.
         * @param fixer       The data mutation logic.
         * @return This builder instance for chaining.
         */
        public MigrationChain fix(int fromVersion, DataFixer fixer) {
            steps.put(fromVersion, fixer);
            return this;
        }

        /**
         * Executes the migration chain and finalizes the configuration state.
         * <p>
         * Reads the current "config_version" from the file, applies all necessary
         * registered fixers sequentially, and saves the updated structure back to disk.
         *
         * @return The parent {@link Config} instance.
         */
        @SuppressWarnings("unchecked")
        public Config apply() {
            int currentVersion = yaml.getInt("config_version", 0);
            if (currentVersion >= targetVersion) return Config.this;

            Object currentData = extractMap(yaml);

            for (Map.Entry<Integer, DataFixer> entry : steps.tailMap(currentVersion, true).entrySet()) {
                if (entry.getKey() >= targetVersion) break;
                currentData = entry.getValue().fix(YamlOps.INSTANCE, currentData);
            }

            if (currentData instanceof Map<?, ?>) {
                Map<String, Object> fixedMap = (Map<String, Object>) currentData;
                fixedMap.put("config_version", targetVersion);
                applyMap(yaml, fixedMap);
                save();
                reload();
            }

            return Config.this;
        }
    }

    /**
     * Represents a specific value within the configuration.
     * Provides methods to get and set data with optional {@link Codec} support.
     *
     * @param <T> The type of the value.
     */
    public class Value<T> {

        /**
         * The configuration path for this value.
         */
        private final String path;
        /**
         * The default value to return if none is found.
         */
        private final T defaultValue;
        /**
         * The optional codec used for complex object mapping.
         */
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
         * @throws RuntimeException If a decoding error occurs.
         */
        @SuppressWarnings({"unchecked"})
        public T get() {
            Object raw = yaml.get(path, defaultValue);
            Object normalized = normalize(raw);
            if (codec != null) {
                DataResult<T> res = codec.decode(YamlOps.INSTANCE, normalized);
                if (res.isError())
                    throw new RuntimeException("Failed to decode value at path '" + path + "': " + res.error().get());
                return res.getOrThrow();
            }
            return (T) normalized;
        }

        /**
         * Sets the value in the configuration. If a codec is present, it will be used
         * to encode the value into a YAML-compatible format.
         *
         * @param value The value to store.
         * @throws RuntimeException If an encoding error occurs.
         */
        public void set(T value) {
            if (codec != null) {
                DataResult<Object> res = codec.encode(YamlOps.INSTANCE, value);
                if (res.isError())
                    throw new RuntimeException("Failed to encode value at path '" + path + "': " + res.error().get());
                yaml.set(path, res.getOrThrow());
                return;
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
         */
        private Object normalize(Object obj) {
            if (obj instanceof ConfigurationSection section) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (String key : section.getKeys(false)) {
                    map.put(key, normalize(section.get(key)));
                }
                return map;
            } else if (obj instanceof List<?> list) {
                List<Object> newList = new ArrayList<>();
                for (Object item : list) {
                    newList.add(normalize(item));
                }
                return newList;
            }
            return obj;
        }
    }
}