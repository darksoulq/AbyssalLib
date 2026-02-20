package com.github.darksoulq.abyssallib.server.permission;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The central authority for managing permissions within AbyssalLib.
 * <p>
 * This manager handles player join/quit cycles, synchronizes internal permission
 * states with the Bukkit engine, and manages persistent storage operations.
 */
public class PermissionManager {

    /**
     * The {@link Plugin} instance responsible for registering attachments.
     */
    private final Plugin plugin;

    /**
     * The {@link PermissionStorage} implementation for data persistence.
     */
    private final PermissionStorage storage;

    /**
     * Map of currently cached {@link PermissionUser} instances by their {@link UUID}.
     */
    private final Map<UUID, PermissionUser> users = new ConcurrentHashMap<>();

    /**
     * Map of active Bukkit {@link PermissionAttachment} objects for online players.
     */
    private final Map<UUID, PermissionAttachment> attachments = new ConcurrentHashMap<>();

    /**
     * Constructs a new PermissionManager and initializes storage systems.
     *
     * @param plugin  The owning plugin.
     * @param storage The storage backend to use.
     */
    public PermissionManager(Plugin plugin, PermissionStorage storage) {
        this.plugin = plugin;
        this.storage = storage;

        this.storage.init();
        this.storage.loadGroups();

        startCleanupTask();
    }

    /**
     * Gets the storage implementation.
     *
     * @return The current {@link PermissionStorage}.
     */
    public PermissionStorage getStorage() {
        return storage;
    }

    /**
     * Validates if a permission string is a valid or recognized node.
     *
     * @param node The permission string to validate.
     * @return {@code true} if the node is valid, {@code false} otherwise.
     */
    public static boolean isValidNode(String node) {
        if (node == null || node.isEmpty()) return false;
        if (node.equals("*") || node.endsWith(".*") || node.startsWith("-")) return true;
        if (Registries.PERMISSIONS.contains(node)) return true;
        return Bukkit.getPluginManager().getPermission(node) != null;
    }

    /**
     * Retrieves a user from the cache or loads them from storage.
     *
     * @param uuid The {@link UUID} of the user.
     * @return The {@link PermissionUser} instance.
     */
    public PermissionUser getUser(UUID uuid) {
        return users.computeIfAbsent(uuid, storage::loadUser);
    }

    /**
     * Retrieves a user from memory only.
     *
     * @param uuid The {@link UUID} of the user.
     * @return The user, or {@code null} if not loaded.
     */
    public PermissionUser getLoadedUser(UUID uuid) {
        return users.get(uuid);
    }

    /**
     * Gets all known users from the storage backend.
     *
     * @return A map of {@link UUID} to last known usernames.
     */
    public Map<UUID, String> getKnownUsers() {
        return storage.getKnownUsers();
    }

    /**
     * Resolves a {@link UUID} from a username.
     *
     * @param name The username to resolve.
     * @return The resolved {@link UUID}, or {@code null} if not found.
     */
    public UUID getUuidFromName(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) return player.getUniqueId();
        return storage.getUuidFromName(name);
    }

    /**
     * Persists a user asynchronously.
     *
     * @param user The {@link PermissionUser} to save.
     */
    public void saveUser(PermissionUser user) {
        storage.saveUserAsync(user);
    }

    /**
     * Persists a group asynchronously.
     *
     * @param group The {@link PermissionGroup} to save.
     */
    public void saveGroup(PermissionGroup group) {
        storage.saveGroupAsync(group);
    }

    /**
     * Deletes a group and removes it from all active holders.
     *
     * @param id The ID of the group to delete.
     */
    public void deleteGroup(String id) {
        Registries.PERMISSION_GROUPS.remove(id);
        for (PermissionUser user : users.values()) {
            user.removeParent(id);
        }
        for (PermissionGroup group : Registries.PERMISSION_GROUPS.getAll().values()) {
            group.removeParent(id);
        }
        storage.deleteGroupAsync(id);
        invalidateAllUsers();
    }

    /**
     * Processes player join logic, updating names and syncing permissions.
     *
     * @param player The joining {@link Player}.
     */
    public void handleJoin(Player player) {
        PermissionUser user = getUser(player.getUniqueId());
        user.setName(player.getName());
        saveUser(user);
        updatePlayer(player.getUniqueId());
    }

    /**
     * Processes player quit logic, cleaning up memory and attachments.
     *
     * @param player The quitting {@link Player}.
     */
    public void handleQuit(Player player) {
        UUID uuid = player.getUniqueId();
        PermissionUser user = users.get(uuid);
        if (user != null) {
            saveUser(user);
        }
        users.remove(uuid);

        PermissionAttachment attachment = attachments.remove(uuid);
        if (attachment != null) {
            try {
                attachment.remove();
            } catch (IllegalArgumentException ignored) {}
        }
    }

    /**
     * Synchronizes internal permission states with the Bukkit engine for a player.
     *
     * @param uuid The {@link UUID} of the player.
     */
    public void updatePlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) return;

        PermissionUser user = getUser(uuid);
        Map<String, Boolean> effective = user.getEffectivePermissions();

        PermissionAttachment attachment = attachments.get(uuid);
        if (attachment == null) {
            attachment = player.addAttachment(plugin);
            attachments.put(uuid, attachment);
        } else {
            for (String perm : attachment.getPermissions().keySet()) {
                attachment.unsetPermission(perm);
            }
        }

        for (Map.Entry<String, Boolean> entry : effective.entrySet()) {
            attachment.setPermission(entry.getKey(), entry.getValue());
        }

        player.recalculatePermissions();
    }

    /**
     * Invalidates the calculated permission cache for all loaded users.
     */
    public void invalidateAllUsers() {
        for (PermissionUser user : users.values()) {
            user.invalidateCache();
        }
    }

    /**
     * Performs a graceful shutdown, saving all data synchronously.
     */
    public void shutdown() {
        for (PermissionUser user : users.values()) {
            storage.saveUser(user);
        }
        for (PermissionGroup group : Registries.PERMISSION_GROUPS.getAll().values()) {
            storage.saveGroup(group);
        }
        storage.shutdown();
    }

    /**
     * Starts the asynchronous task to purge expired nodes and update holders.
     */
    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            boolean groupChanged = false;
            for (PermissionGroup group : Registries.PERMISSION_GROUPS.getAll().values()) {
                if (group.clearExpired()) {
                    groupChanged = true;
                    saveGroup(group);
                }
            }
            if (groupChanged) {
                invalidateAllUsers();
            }

            for (PermissionUser user : users.values()) {
                if (user.clearExpired()) {
                    saveUser(user);
                    Bukkit.getScheduler().runTask(plugin, () -> updatePlayer(user.getUuid()));
                }
            }
        }, 200L, 200L);
    }
}