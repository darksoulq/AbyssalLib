package com.github.darksoulq.abyssallib.server.permission;

import java.util.Objects;

/**
 * Represents a specific permission entry or group inheritance entry.
 * <p>
 * A Node can be permanent or temporary. When used in a {@link PermissionHolder},
 * it determines whether a specific permission string is granted or denied,
 * or whether a group is inherited as a parent.
 */
public class Node {

    /**
     * The unique key identifying the permission or group (e.g., "minecraft.command.tp").
     */
    private final String key;

    /**
     * The boolean value of the node. If {@code true}, the permission is granted;
     * if {@code false}, it is explicitly denied.
     */
    private final boolean value;

    /**
     * The Unix timestamp (in milliseconds) at which this node expires.
     * A value of {@code 0L} indicates a permanent node.
     */
    private final long expiry;

    /**
     * Constructs a Node with a specific key, value, and expiry.
     *
     * @param key    The permission or group key.
     * @param value  The boolean state of the node.
     * @param expiry The expiration timestamp in milliseconds.
     */
    public Node(String key, boolean value, long expiry) {
        this.key = key;
        this.value = value;
        this.expiry = expiry;
    }

    /**
     * Constructs a permanent Node with a specific key and value.
     *
     * @param key   The permission or group key.
     * @param value The boolean state of the node.
     */
    public Node(String key, boolean value) {
        this(key, value, 0L);
    }

    /**
     * Constructs a permanent Node with a specific key, defaulting the value to {@code true}.
     *
     * @param key The permission or group key.
     */
    public Node(String key) {
        this(key, true, 0L);
    }

    /**
     * Gets the identifier key of this node.
     *
     * @return The key string.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the boolean state of this node.
     *
     * @return {@code true} if granted/active, {@code false} if denied.
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Gets the expiration timestamp.
     *
     * @return The expiry time in milliseconds, or {@code 0L} if permanent.
     */
    public long getExpiry() {
        return expiry;
    }

    /**
     * Checks if this node has a set expiration date.
     *
     * @return {@code true} if the node is temporary, {@code false} if permanent.
     */
    public boolean isTemporary() {
        return expiry > 0;
    }

    /**
     * Checks if the current system time has passed the expiration timestamp.
     *
     * @return {@code true} if temporary and expired, {@code false} otherwise.
     */
    public boolean hasExpired() {
        return isTemporary() && System.currentTimeMillis() >= expiry;
    }

    /**
     * Compares this node to another object based on the key.
     *
     * @param o The object to compare.
     * @return {@code true} if the keys match.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return key.equals(node.key);
    }

    /**
     * Generates a hash code based on the node key.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}