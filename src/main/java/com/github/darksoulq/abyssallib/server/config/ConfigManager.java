package com.github.darksoulq.abyssallib.server.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class ConfigManager<T> {
    private final Class<T> configClass;
    private final YamlConfigurationLoader loader;

    private T instance;

    public ConfigManager(Path path, Class<T> configClass) {
        this.configClass = configClass;
        this.loader = YamlConfigurationLoader.builder()
                .path(path)
                .build();
    }

    public void load() {
        try {
            CommentedConfigurationNode root = loader.load();

            this.instance = root.get(configClass);
            if (this.instance == null) {
                try {
                    this.instance = configClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException("Could not instantiate config class", e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        save();
    }

    public void save() {
        try {
            CommentedConfigurationNode root = loader.load();
            root.set(configClass, instance);
            loader.save(root);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Consumer<T> updater) {
        updater.accept(instance);
        save();
    }

    public T get() {
        return instance;
    }

    public static Path resolvePath(String name, String id, String subFolder) {
        return Path.of("config", id, subFolder, name);
    }
    public static Path resolvePath(String name, String id) {
        return Path.of("config", id, name);
    }

}
