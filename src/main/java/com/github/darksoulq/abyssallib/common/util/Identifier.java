package com.github.darksoulq.abyssallib.common.util;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a unique identifier used to categorize and locate resources or data.
 * <p>
 * This class supports two formats:
 * <ul>
 * <li><b>Namespaced:</b> {@code namespace:path} (Standard Minecraft/Bukkit format)</li>
 * <li><b>Keyed:</b> {@code key:namespace:path} (Extended format for specific sub-categorization)</li>
 * </ul>
 * All components are restricted to lowercase alphanumeric characters, dots, underscores, and hyphens.
 */
public class Identifier {
    /** The optional primary category key. */
    private final String key;
    /** The namespace (usually the plugin or mod ID). */
    private final String namespace;
    /** The path representing the specific resource. */
    private final String path;

    /**
     * Private constructor for internal instantiation.
     *
     * @param key       The primary key (may be null).
     * @param namespace The resource namespace.
     * @param path      The resource path.
     */
    private Identifier(String key, String namespace, String path) {
        this.key = key;
        this.namespace = namespace;
        this.path = path;
    }

    /**
     * Parses a string into an Identifier.
     *
     * @param value The raw string to parse (format 'ns:path' or 'key:ns:path').
     * @return A new Identifier instance.
     * @throws IllegalArgumentException If the format is invalid or contains illegal characters.
     */
    public static @NotNull Identifier of(@NotNull String value) {
        String[] parts = value.split(":", 3);

        if (isValid2Part(value)) {
            return new Identifier(null, parts[0], parts[1]);
        } else if (isValid3Part(value)) {
            return new Identifier(parts[0], parts[1], parts[2]);
        } else {
            throw new IllegalArgumentException(
                "Identifier must be in format 'namespace:path' or 'key:namespace:path'"
            );
        }
    }

    /**
     * Creates a standard 2-part Namespaced Identifier.
     *
     * @param namespace The namespace string.
     * @param path      The path string.
     * @return A new Identifier instance.
     * @throws IllegalArgumentException If namespace or path contain invalid characters.
     */
    public static @NotNull Identifier of(@NotNull String namespace, @NotNull String path) {
        if (!isValid(namespace) || !isValid(path)) {
            throw new IllegalArgumentException("Invalid namespace or path: " + namespace + ":" + path);
        }
        return new Identifier(null, namespace, path);
    }

    /**
     * Creates an extended 3-part Keyed Identifier.
     *
     * @param key       The primary key category.
     * @param namespace The namespace string.
     * @param path      The path string.
     * @return A new Identifier instance.
     * @throws IllegalArgumentException If any component contains invalid characters.
     */
    public static @NotNull Identifier of(@NotNull String key, @NotNull String namespace, @NotNull String path) {
        if (!isValid(key) || !isValid(namespace) || !isValid(path)) {
            throw new IllegalArgumentException("Invalid key/namespace/path: " + key + ":" + namespace + ":" + path);
        }
        return new Identifier(key, namespace, path);
    }

    /**
     * @return The primary key component, or null if this is a standard 2-part Identifier.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @return The namespace component.
     */
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * @return The path component.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Converts the identifier to its string representation.
     *
     * @return String in format {@code key:ns:path} or {@code ns:path}.
     */
    @Override
    public @NotNull String toString() {
        return key != null ? key + ":" + namespace + ":" + path : namespace + ":" + path;
    }

    /**
     * Converts this Identifier to a Bukkit {@link NamespacedKey}.
     * <p>
     * Note: This only uses the {@code namespace} and {@code path} components.
     *
     * @return A new NamespacedKey.
     */
    public @NotNull NamespacedKey asNamespacedKey() {
        return new NamespacedKey(namespace, path);
    }

    /**
     * Converts this Identifier to an Adventure {@link Key}.
     * <p>
     * Note: This only uses the {@code namespace} and {@code path} components.
     *
     * @return A new Adventure Key.
     */
    public @NotNull Key asKey() {
        return Key.key(namespace, path);
    }

    /**
     * Validates an individual identifier component.
     *
     * @param input The string component to validate.
     * @return True if the string matches {@code [a-z0-9._-]}.
     */
    public static boolean isValid(String input) {
        return input != null && input.matches("[a-z0-9._-]+");
    }

    /**
     * Validates if a string is a valid 2-part Identifier (ns:path).
     *
     * @param input The raw string to check.
     * @return True if valid.
     */
    public static boolean isValid2Part(String input) {
        if (input == null) return false;
        String[] parts = input.split(":", 2);
        return parts.length == 2 && isValid(parts[0]) && isValid(parts[1]);
    }

    /**
     * Validates if a string is a valid 3-part Identifier (key:ns:path).
     *
     * @param input The raw string to check.
     * @return True if valid.
     */
    public static boolean isValid3Part(String input) {
        if (input == null) return false;
        String[] parts = input.split(":", 3);
        return parts.length == 3 && isValid(parts[0]) && isValid(parts[1]) && isValid(parts[2]);
    }

    /**
     * Compares this Identifier to another object for equality.
     *
     * @param o The object to compare.
     * @return True if all components (key, namespace, path) match.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier other)) return false;
        return Objects.equals(this.key, other.key)
            && this.namespace.equals(other.namespace)
            && this.path.equals(other.path);
    }

    /**
     * Generates a hash code for this Identifier.
     *
     * @return Hash code based on key, namespace, and path.
     */
    @Override
    public int hashCode() {
        return Objects.hash(key, namespace, path);
    }
}