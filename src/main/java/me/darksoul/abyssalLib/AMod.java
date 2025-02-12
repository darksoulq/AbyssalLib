package me.darksoul.abyssalLib;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.ConcurrentHashMap;

public class AMod {
    private static final ConcurrentHashMap<Plugin, String> _namespaces = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Plugin> _mods = new ConcurrentHashMap<>();

    public static void register(String namespace, Plugin plugin) {
        _mods.put(namespace, plugin);
    }

    public static ConcurrentHashMap<String, Plugin> getMods() {
        return _mods;
    }

    public static ConcurrentHashMap<Plugin, String> getNamespaces() {
        return _namespaces;
    }

    public static Plugin getPlugin(String namespace) {
        return _mods.get(namespace);
    }

    public static String getNamespace(Plugin plugin) {
        return _namespaces.get(plugin);
    }
}
