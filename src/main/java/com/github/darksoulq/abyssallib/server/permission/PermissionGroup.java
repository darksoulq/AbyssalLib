package com.github.darksoulq.abyssallib.server.permission;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.Registries;

/**
 * Represents a group that can contain permissions and be inherited by users or other groups.
 */
public class PermissionGroup extends PermissionHolder {

    /**
     * The unique string identifier for this group.
     */
    private final String id;

    /**
     * The priority weight used during permission calculation.
     */
    private int weight;

    /**
     * Constructs a group with the specified ID.
     *
     * @param id The group identifier.
     */
    public PermissionGroup(String id) {
        this.id = id;
        this.weight = 0;
    }

    /**
     * Gets the group identifier.
     *
     * @return The ID string.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the weight of this group.
     *
     * @return The integer weight.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets the group weight and invalidates the cache.
     *
     * @param weight The new weight.
     */
    public void setWeight(int weight) {
        this.weight = weight;
        invalidateCache();
    }

    /**
     * Invalidates the caches of all users and inheriting groups when this group changes.
     */
    @Override
    protected void onCacheInvalidate() {
        if (AbyssalLib.PERMISSION_MANAGER != null) {
            AbyssalLib.PERMISSION_MANAGER.invalidateAllUsers();
        }
        for (PermissionGroup group : Registries.PERMISSION_GROUPS.getAll().values()) {
            if (group != this && group.hasParent(this.id)) {
                group.invalidateCache();
            }
        }
    }

    /**
     * Triggers a save operation through the global permission manager.
     */
    @Override
    public void save() {
        if (AbyssalLib.PERMISSION_MANAGER != null) {
            AbyssalLib.PERMISSION_MANAGER.saveGroup(this);
        }
    }
}