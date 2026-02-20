package com.github.darksoulq.abyssallib.server.permission;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Represents a persistent user with an assigned set of permissions and parent groups.
 */
public class PermissionUser extends PermissionHolder {

    /**
     * The unique identifier for this user.
     */
    private final UUID uuid;

    /**
     * The last known username of this user.
     */
    private String name;

    /**
     * Constructs a user with the specified UUID.
     *
     * @param uuid The user's {@link UUID}.
     */
    public PermissionUser(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the unique identifier.
     *
     * @return The {@link UUID}.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the username, fetching it from the system if not cached.
     *
     * @return The username string.
     */
    public String getName() {
        if (name == null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            name = op.getName() != null ? op.getName() : uuid.toString();
        }
        return name;
    }

    /**
     * Sets the cached username.
     *
     * @param name The username to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the player's live Bukkit permissions when the cache is invalidated.
     */
    @Override
    protected void onCacheInvalidate() {
        if (AbyssalLib.PERMISSION_MANAGER != null) {
            AbyssalLib.PERMISSION_MANAGER.updatePlayer(uuid);
        }
    }

    /**
     * Triggers a save operation through the global permission manager.
     */
    @Override
    public void save() {
        if (AbyssalLib.PERMISSION_MANAGER != null) {
            AbyssalLib.PERMISSION_MANAGER.saveUser(this);
        }
    }
}