package com.github.darksoulq.abyssallib.common.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Config {

    private final File file;
    private final YamlConfiguration yaml;
    private final Map<String, List<String>> comments = new HashMap<>();

    public Config(String pluginId, String name, String subfolder) {
        this.file = Path.of("config", pluginId, subfolder, name + ".yml").toFile();
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }
    public Config(String pluginId, String name) {
        this.file = Path.of("config", pluginId, name + ".yml").toFile();
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    public <T> Value<T> value(String path, T defaultValue) {
        Value<T> val = new Value<>(path, defaultValue);
        if (!yaml.contains(path)) val.set(defaultValue);
        return val;
    }

    public void addComment(String path, String... commentLines) {
        comments.put(path, Arrays.asList(commentLines));
    }

    public void save() throws IOException {
        yaml.save(file);
        writeComments();
    }

    public void reload() {
        yaml.options().copyDefaults(true);
        try {
            yaml.load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

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

    private int countLeadingSpaces(String line) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == ' ') count++;
        return count;
    }

    public class Value<T> {

        private final String path;
        private final T defaultValue;

        public Value(String path, T defaultValue) {
            this.path = path;
            this.defaultValue = defaultValue;
        }

        @SuppressWarnings("unchecked")
        public T get() {
            return (T) yaml.get(path, defaultValue);
        }

        public void set(T value) {
            yaml.set(path, value);
        }

        public Value<T> withComment(String... comments) {
            addComment(path, comments);
            return this;
        }
    }
}
