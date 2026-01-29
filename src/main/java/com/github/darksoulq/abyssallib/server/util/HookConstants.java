package com.github.darksoulq.abyssallib.server.util;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class HookConstants {
    public enum Plugin {
        RSPM("ResourcePackManager"),
        NEXO("Nexo"),
        IA("ItemsAdder"),
        PLACEHOLDER_API("PlaceholderAPI");

        public final String pluginName;
        Plugin(String name) {
            this.pluginName = name;
        }
    }
    private static final Map<Plugin, Boolean> PLUGIN_STATE = new HashMap<>();

    public static void load() {
        for (Plugin val : Plugin.values()) {
            boolean enabled = Bukkit.getPluginManager().isPluginEnabled(val.pluginName);
            PLUGIN_STATE.put(val, enabled);
            String prefix = enabled ? "Enabled" : "Failed to load";
            AbyssalLib.getInstance().getLogger().info(prefix + " hook for " + val.pluginName);
        }
    }
    public static boolean isEnabled(Plugin pl) {
        return PLUGIN_STATE.get(pl);
    }
}
