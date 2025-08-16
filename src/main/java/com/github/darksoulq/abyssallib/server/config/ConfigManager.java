package com.github.darksoulq.abyssallib.server.config;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager<T> {

    private final Class<T> configClass;
    private final Path path;
    private final ConfigurationLoader<?> loader;
    private T config;
    private final boolean repopulate;

    public ConfigManager(Path path, Class<T> configClass, boolean repopulate) {
        this.path = path;
        this.configClass = configClass;
        this.repopulate = repopulate;
        this.loader = YamlConfigurationLoader.builder()
                .path(path)
                .build();
    }

    public void load() {
        try {
            Files.createDirectories(path.getParent());

            if (Files.notExists(path)) {
                ConfigurationNode root = loader.createNode();
                root.set(configClass, configClass.getDeclaredConstructor().newInstance());
                loader.save(root);
            }

            ConfigurationNode root = loader.load();
            config = root.get(configClass);

            if (repopulate) {
                root.set(configClass, config);
                loader.save(root);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + path, e);
        }
    }

    public void reload() {
        load();
    }

    public void save() {
        try {
            ConfigurationNode root = loader.createNode();
            root.set(configClass, config);
            loader.save(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save config: " + path, e);
        }
    }

    public T get() {
        return config;
    }

    public static Path resolvePath(String name, String id, String subFolder) {
        return Path.of("config", id, subFolder, name + ".yml");
    }
    public static Path resolvePath(String name, String id) {
        return Path.of("config", id, name + ".yml");
    }
}
