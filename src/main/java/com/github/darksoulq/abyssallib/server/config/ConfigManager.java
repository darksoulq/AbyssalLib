package com.github.darksoulq.abyssallib.server.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class ConfigManager<T> {

    private final Path path;
    private final Class<T> configClass;
    private final YamlConfigurationLoader loader;
    private T instance;

    public ConfigManager(Path path, Class<T> configClass) {
        this.path = path;
        this.configClass = configClass;
        this.loader = YamlConfigurationLoader.builder()
                .path(path)
                .nodeStyle(NodeStyle.FLOW)
                .defaultOptions(ConfigurationOptions.defaults().shouldCopyDefaults(true))
                .build();
    }

    public void load(boolean repopulateMissing) throws IOException {
        CommentedConfigurationNode root = loader.load();

        root.options().shouldCopyDefaults(repopulateMissing);

        this.instance = root.get(configClass);
        if (this.instance == null) {
            try {
                this.instance = configClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("Could not instantiate config class", e);
            }
        }

        save();
    }

    public void save() throws IOException {
        CommentedConfigurationNode root = loader.createNode();
        root.set(configClass, instance);
        loader.save(root);
    }

    public T get() {
        return instance;
    }

    public static Path resolvePath(String name, String id, String subFolder) {
        return Path.of("config", id, subFolder, name + ".yml");
    }
    public static Path resolvePath(String name, String id) {
        return Path.of("config", id, name + ".yml");
    }

}
