package com.github.darksoulq.abyssallib.server.permission;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a structured permission node with defaults and child relationships.
 * This class facilitates the fluent definition of permissions before they are
 * registered with the Bukkit PluginManager.
 */
public class PermissionNode {

    /**
     * The unique string identifier for the permission (e.g., "abyssal.admin").
     */
    private final String node;

    /**
     * The default state of the permission if not explicitly set.
     * Defaults to {@link PermissionDefault#OP}.
     */
    private PermissionDefault defaultValue = PermissionDefault.OP;

    /**
     * A brief description explaining what the permission grants access to.
     */
    private String description = "";

    /**
     * A map of child permission nodes and their inherited boolean values.
     */
    private final Map<String, Boolean> children = new LinkedHashMap<>();

    /**
     * The cached Bukkit {@link Permission} object generated from this node.
     */
    private Permission bukkitPermission;

    /**
     * Constructs a new PermissionNode.
     *
     * @param node The raw permission string.
     */
    public PermissionNode(String node) {
        this.node = node;
    }

    /**
     * Sets the default value for this permission.
     *
     * @param defaultValue The {@link PermissionDefault} behavior.
     * @return This node instance for chaining.
     */
    public PermissionNode defaultValue(PermissionDefault defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Sets the description for this permission.
     *
     * @param description A string describing the permission.
     * @return This node instance for chaining.
     */
    public PermissionNode description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Adds a child node by string name.
     *
     * @param childNode The string of the child permission.
     * @param value     The value the child receives when this node is true.
     * @return This node instance for chaining.
     */
    public PermissionNode child(String childNode, boolean value) {
        this.children.put(childNode, value);
        return this;
    }

    /**
     * Adds a child node using another PermissionNode instance.
     *
     * @param childNode The PermissionNode to inherit.
     * @param value     The value the child receives when this node is true.
     * @return This node instance for chaining.
     */
    public PermissionNode child(PermissionNode childNode, boolean value) {
        this.children.put(childNode.getNode(), value);
        return this;
    }

    /**
     * @return The raw permission node string.
     */
    public String getNode() {
        return node;
    }

    /**
     * @return The {@link PermissionDefault} value of this node.
     */
    public PermissionDefault getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return The description of this permission.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return An unmodifiable map containing the child nodes and their values.
     */
    public Map<String, Boolean> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    /**
     * Checks if a permissible object (like a Player) has this specific node.
     *
     * @param permissible The entity to check.
     * @return True if the entity has the permission.
     */
    public boolean has(Permissible permissible) {
        return permissible.hasPermission(node);
    }

    /**
     * Converts this node into a Bukkit {@link Permission} object.
     * <p>
     * This method utilizes lazy initialization to cache the result.
     *
     * @return The Bukkit Permission representation.
     */
    public Permission toBukkit() {
        if (bukkitPermission == null) {
            bukkitPermission = new Permission(node, description, defaultValue, children);
        }
        return bukkitPermission;
    }

    /**
     * @return The raw node string.
     */
    @Override
    public String toString() {
        return node;
    }
}