package me.darksoul.abyssalLib.resource;

import me.darksoul.abyssalLib.AMod;
import me.darksoul.abyssalLib.util.FileUtils;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AResource {
    private static final ConcurrentHashMap<String, List<String>> _resources = new ConcurrentHashMap<>();

    public static void loadResources(Plugin plugin) {
        List<String> files = FileUtils.getFilePathList(plugin, "assets/");
        files.forEach((file) -> {
            putInMap(AMod.getNamespace(plugin), file);
        });
    }

    private static void putInMap(String namespace, String path) {
        _resources.computeIfAbsent(namespace, k -> new java.util.ArrayList<>()).add(path);
    }

    public static ConcurrentHashMap<String, List<String>> getResources() {
        return _resources;
    }
}