package com.github.darksoulq.abyssallib.server.permission;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides a scoped container for registering permissions under a plugin-specific namespace.
 */
public class PermissionNamespace {

    /**
     * The unique ID of the plugin (e.g., "abyssallib").
     */
    private final String pluginId;

    /**
     * Internal map tracking registered paths and their corresponding PermissionNode holders.
     */
    private final Map<String, Holder<PermissionNode>> entries = new LinkedHashMap<>();

    /**
     * Private constructor for internal factory use.
     *
     * @param pluginId The namespace identifier.
     */
    private PermissionNamespace(String pluginId) {
        this.pluginId = pluginId;
    }

    /**
     * Creates a new namespace for a specific plugin.
     *
     * @param pluginId The ID of the plugin.
     * @return A new PermissionNamespace instance.
     */
    public static PermissionNamespace create(String pluginId) {
        return new PermissionNamespace(pluginId);
    }

    /**
     * Registers a new permission node within this namespace.
     *
     * @param path     The sub-path (e.g., "admin.reload").
     * @param supplier A function that receives the full node string and returns a node builder.
     * @return A {@link Holder} for the registered node.
     * @throws IllegalStateException If the path has already been registered.
     */
    public Holder<PermissionNode> register(String path, Function<String, PermissionNode> supplier) {
        if (entries.containsKey(path)) {
            throw new IllegalStateException("Duplicate permission registration: " + pluginId + "." + path);
        }
        String nodeStr = pluginId + "." + path;
        Holder<PermissionNode> holder = new Holder<>(() -> supplier.apply(nodeStr));
        entries.put(path, holder);
        return holder;
    }

    /**
     * Finalizes the registration process by adding all nodes to the AbyssalLib
     * registry and the Bukkit {@link PluginManager}.
     * <p>
     * This method clears the pending entries map after execution.
     */
    public void apply() {
        PluginManager pm = Bukkit.getPluginManager();
        for (Map.Entry<String, Holder<PermissionNode>> entry : entries.entrySet()) {
            PermissionNode node = entry.getValue().get();
            Registries.PERMISSIONS.register(node.getNode(), node);

            if (pm.getPermission(node.getNode()) == null) {
                pm.addPermission(node.toBukkit());
            }
        }
        entries.clear();
    }

    /**
     * @return An unmodifiable collection of all permission holders in this namespace.
     */
    public Collection<Holder<PermissionNode>> getEntries() {
        return Collections.unmodifiableCollection(entries.values());
    }

    /**
     * @return The plugin ID associated with this namespace.
     */
    public String getPluginId() {
        return pluginId;
    }
}