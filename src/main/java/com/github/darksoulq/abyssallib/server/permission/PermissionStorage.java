package com.github.darksoulq.abyssallib.server.permission;

import java.util.Map;
import java.util.UUID;

/**
 * Interface defining the contract for permission data persistence.
 */
public interface PermissionStorage {

    /**
     * Initializes the storage backend (e.g., database connection).
     */
    void init();

    /**
     * Loads all defined groups into memory.
     */
    void loadGroups();

    /**
     * Loads a specific user from storage.
     *
     * @param uuid The {@link UUID} of the user.
     * @return The loaded {@link PermissionUser}.
     */
    PermissionUser loadUser(UUID uuid);

    /**
     * Resolves a UUID from a stored username.
     *
     * @param name The username.
     * @return The {@link UUID}, or {@code null} if not stored.
     */
    UUID getUuidFromName(String name);

    /**
     * Gets a map of all known user UUIDs and names.
     *
     * @return The user map.
     */
    Map<UUID, String> getKnownUsers();

    /**
     * Saves a group synchronously.
     *
     * @param group The group to save.
     */
    void saveGroup(PermissionGroup group);

    /**
     * Saves a user synchronously.
     *
     * @param user The user to save.
     */
    void saveUser(PermissionUser user);

    /**
     * Saves a group asynchronously.
     *
     * @param group The group to save.
     */
    void saveGroupAsync(PermissionGroup group);

    /**
     * Saves a user asynchronously.
     *
     * @param user The user to save.
     */
    void saveUserAsync(PermissionUser user);

    /**
     * Deletes a group synchronously.
     *
     * @param id The ID of the group.
     */
    void deleteGroup(String id);

    /**
     * Deletes a group asynchronously.
     *
     * @param id The ID of the group.
     */
    void deleteGroupAsync(String id);

    /**
     * Closes the storage backend.
     */
    void shutdown();
}