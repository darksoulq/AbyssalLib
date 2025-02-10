package me.darksoul.abyssalLib.resource;

import me.darksoul.abyssalLib.AMod;
import me.darksoul.abyssalLib.util.FileUtils;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class AResource {
    private static final ConcurrentHashMap<String, List<String>> _resources = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Plugin> _mods = new ConcurrentHashMap<>();

    public static void loadResources(Plugin plugin) {
        _mods.put(((AMod) plugin).getNamespace(), plugin);
        CompletableFuture<Void> texturesFuture = loadItemTextures(plugin, ((AMod) plugin).getNamespace());
        CompletableFuture<Void> modelsFuture = loadItemModels(plugin, ((AMod) plugin).getNamespace());
        CompletableFuture<Void> registriesFuture = loadItemRegistries(plugin, ((AMod) plugin).getNamespace());

        CompletableFuture.allOf(texturesFuture, modelsFuture, registriesFuture).join();
    }

    private static CompletableFuture<Void> loadItemTextures(Plugin plugin, String namespace) {
        return FileUtils.getFilePathList(plugin, "assets/textures/item")
                .thenAcceptAsync(files -> files.forEach(file -> {
                    if (file.startsWith("assets/")) {
                        putInMap(namespace, file.replaceFirst("^assets/", ""));
                    }
                }));
    }

    private static CompletableFuture<Void> loadItemModels(Plugin plugin, String namespace) {
        return FileUtils.getFilePathList(plugin, "assets/textures/models")
                .thenAcceptAsync(files -> files.forEach(file -> {
                    if (file.startsWith("assets/")) {
                        putInMap(namespace, file.replaceFirst("^assets/", ""));
                    }
                }));
    }

    private static CompletableFuture<Void> loadItemRegistries(Plugin plugin, String namespace) {
        return FileUtils.getFilePathList(plugin, "assets/items")
                .thenAcceptAsync(files -> files.forEach(file -> {
                    if (file.startsWith("assets/")) {
                        putInMap(namespace, file.replaceFirst("^assets/", ""));
                    }
                }));
    }

    private static void putInMap(String namespace, String path) {
        _resources.computeIfAbsent(namespace, k -> new java.util.ArrayList<>()).add(path);
    }

    public static ConcurrentHashMap<String, List<String>> getResources() {
        return _resources;
    }
    public static ConcurrentHashMap<String, Plugin> getMods() {
        return _mods;
    }
}
