package com.github.darksoulq.abyssallib.server.permission;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An abstract base for objects that can hold permissions and inherit from
 * parent nodes (typically groups).
 */
public abstract class PermissionHolder {

    /**
     * Map of permission keys to their respective {@link Node} data.
     */
    protected final Map<String, Node> permissions = new ConcurrentHashMap<>();

    /**
     * Map of parent group keys to their respective {@link Node} data.
     */
    protected final Map<String, Node> parents = new ConcurrentHashMap<>();

    /**
     * Cached results of permission calculation to avoid redundant processing.
     */
    protected Map<String, Boolean> cachedEffectivePermissions;

    /**
     * Sets a permission node on this holder and invalidates the cache.
     *
     * @param node The {@link Node} to set.
     */
    public void setPermission(Node node) {
        if (!PermissionManager.isValidNode(node.getKey())) return;
        permissions.put(node.getKey(), node);
        invalidateCache();
    }

    /**
     * Removes a permission node by its key.
     *
     * @param key The node key to remove.
     */
    public void unsetPermission(String key) {
        if (permissions.remove(key) != null) {
            invalidateCache();
        }
    }

    /**
     * Adds a parent group to this holder via a Node.
     *
     * @param node The node representing group inheritance.
     */
    public void addParent(Node node) {
        parents.put(node.getKey(), node);
        invalidateCache();
    }

    /**
     * Removes a parent group by its key.
     *
     * @param key The key of the parent to remove.
     */
    public void removeParent(String key) {
        if (parents.remove(key) != null) {
            invalidateCache();
        }
    }

    /**
     * Checks if this holder has a direct (non-inherited) permission set to true.
     *
     * @param key The permission key.
     * @return True if directly set, not expired, and true.
     */
    public boolean hasDirectPermission(String key) {
        Node node = permissions.get(key);
        return node != null && !node.hasExpired() && node.getValue();
    }

    /**
     * Checks if this holder has a specific parent group assigned.
     *
     * @param key The group key.
     * @return True if assigned and not expired.
     */
    public boolean hasParent(String key) {
        Node node = parents.get(key);
        return node != null && !node.hasExpired();
    }

    /**
     * @return An unmodifiable collection of direct permission nodes.
     */
    public Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(permissions.values());
    }

    /**
     * @return An unmodifiable collection of parent group nodes.
     */
    public Collection<Node> getParentNodes() {
        return Collections.unmodifiableCollection(parents.values());
    }

    /**
     * Removes nodes and parents that have exceeded their expiry time.
     *
     * @return True if any entries were removed.
     */
    public boolean clearExpired() {
        boolean changed = false;
        long now = System.currentTimeMillis();

        changed |= permissions.values().removeIf(node -> node.isTemporary() && now >= node.getExpiry());
        changed |= parents.values().removeIf(node -> node.isTemporary() && now >= node.getExpiry());

        if (changed) {
            invalidateCache();
        }
        return changed;
    }

    /**
     * Clears the effective permission cache and triggers custom invalidation logic.
     */
    public void invalidateCache() {
        this.cachedEffectivePermissions = null;
        onCacheInvalidate();
    }

    /**
     * Abstract hook for subclasses to react when the permission cache is cleared.
     */
    protected abstract void onCacheInvalidate();

    /**
     * Abstract hook for subclasses to handle data persistence.
     */
    public abstract void save();

    /**
     * Calculates and returns the map of all permissions active for this holder,
     * including inherited ones.
     *
     * @return A map of active permission keys to their boolean values.
     */
    public Map<String, Boolean> getEffectivePermissions() {
        if (cachedEffectivePermissions == null) {
            cachedEffectivePermissions = PermissionCalculator.calculateEffective(this);
        }
        return cachedEffectivePermissions;
    }
}