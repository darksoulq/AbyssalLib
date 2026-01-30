package com.github.darksoulq.abyssallib.server.util;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for managing and checking the status of external plugin integrations.
 * <p>
 * This class scans the server's plugin manager to determine which supported
 * plugins are currently enabled, allowing for safe cross-plugin functionality
 * without causing {@link NoClassDefFoundError}s.
 */
public class HookConstants {

    /**
     * An enumeration of supported external plugins for which the library
     * provides specialized hooks.
     */
    public enum Plugin {
        /** Integration with ResourcePackManager. */
        RSPM("ResourcePackManager"),
        /** Integration with the Nexo items/mechanics plugin. */
        NEXO("Nexo"),
        /** Integration with the ItemsAdder custom items plugin. */
        IA("ItemsAdder"),
        /** Integration with the PlaceholderAPI string expansion utility. */
        PLACEHOLDER_API("PlaceholderAPI");

        /** The exact name of the plugin as defined in its {@code plugin.yml}. */
        public final String pluginName;

        /**
         * Constructs a Plugin enum constant.
         *
         * @param name The plugin's name.
         */
        Plugin(String name) {
            this.pluginName = name;
        }
    }

    /** * A thread-safe internal map storing the enabled state of each
     * supported {@link Plugin}.
     */
    private static final Map<Plugin, Boolean> PLUGIN_STATE = new HashMap<>();

    /**
     * Initializes the hook states by checking the Bukkit {@link org.bukkit.plugin.PluginManager}.
     * <p>
     * This method should be called during the plugin's enable phase. It logs
     * the status of each discovered hook to the server console.
     */
    public static void load() {
        for (Plugin val : Plugin.values()) {
            boolean enabled = Bukkit.getPluginManager().isPluginEnabled(val.pluginName);
            PLUGIN_STATE.put(val, enabled);
            String prefix = enabled ? "Enabled" : "Failed to load";
            AbyssalLib.getInstance().getLogger().info(prefix + " hook for " + val.pluginName);
        }
    }

    /**
     * Checks if a specific external plugin hook is currently active.
     *
     * @param pl The {@link Plugin} to check.
     * @return {@code true} if the plugin is installed and enabled on the server.
     */
    public static boolean isEnabled(Plugin pl) {
        return PLUGIN_STATE.getOrDefault(pl, false);
    }
}