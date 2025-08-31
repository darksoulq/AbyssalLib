package com.github.darksoulq.abyssallib.common.config;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class ConfigManager<T> {

    private final Class<T> configClass;
    private final YamlConfigurationLoader loader;

    private CommentedConfigurationNode root;
    private T instance;

    public ConfigManager(Path path, Class<T> configClass) {
        this.configClass = configClass;
        this.loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(path)
                .build();
    }

    public void load() {
        try {
            this.root = loader.load();
            this.instance = root.get(configClass);

            if (this.instance == null) {
                log("No existing config, instantiating " + configClass.getSimpleName());
                try {
                    this.instance = configClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException("Could not instantiate config class", e);
                }
            }

            save();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void save() {
        try {
            if (root == null) {
                log("Root node is null, creating new root before save");
                root = loader.createNode();
            }

            root.set(configClass, instance);

            log("Saving config for " + configClass.getSimpleName());
            for (var child : root.childrenMap().entrySet()) {
                log(" - " + child.getKey() + ": " + child.getValue().raw());
            }

            loader.save(root);
            log("Config saved successfully");
        } catch (ConfigurateException e) {
            throw new RuntimeException("Failed to save config", e);
        }
    }

    public void update(Consumer<T> updater) {
        log("Updating config object...");
        updater.accept(instance);
        save();
    }

    public T get() {
        return instance;
    }

    private void log(String msg) {
        AbyssalLib.getInstance().getLogger()
                .info(msg);
    }

    public static Path resolvePath(String name, String id, String subFolder) {
        return Path.of("config", id, subFolder, name);
    }

    public static Path resolvePath(String name, String id) {
        return Path.of("config", id, name);
    }
}
