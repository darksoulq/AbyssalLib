package com.github.darksoulq.abyssallib.server.util;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@ApiStatus.Internal
public final class Integrations {

    private static final Map<String, Consumer<Plugin>> PENDING_HOOKS = new ConcurrentHashMap<>();

    private Integrations() {}

    public static void when(String pluginName, Consumer<Plugin> action) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null && plugin.isEnabled()) {
            execute(pluginName, plugin, action);
        } else {
            PENDING_HOOKS.put(pluginName, action);
        }
    }

    public static void resolve(Plugin plugin) {
        if (!plugin.isEnabled()) return; 

        String name = plugin.getName();
        Consumer<Plugin> action = PENDING_HOOKS.remove(name);
        if (action != null) {
            execute(name, plugin, action);
        }
    }

    private static void execute(String name, Plugin plugin, Consumer<Plugin> action) {
        Try.run(() -> action.accept(plugin))
           .onFailure(e -> AbyssalLib.LOGGER.severe("Failed to hook into integration '" + name + "': " + e.getMessage()));
    }
}