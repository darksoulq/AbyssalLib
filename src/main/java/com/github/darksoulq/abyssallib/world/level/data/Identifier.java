package com.github.darksoulq.abyssallib.world.level.data;

import net.kyori.adventure.key.Key;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a resource identifier in the format {@code namespace:path}.
 */
public class Identifier {

    /** The namespace */
    private final String namespace;
    /** The path */
    private final String path;

    /**
     * Private constructor to enforce usage of the {@link #of(String)} or {@link #of(String, String)} factory methods.
     */
    private Identifier(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    /**
     * Creates an {@link Identifier} from a full string like {@code namespace:path}.
     *
     * @param value the string identifier
     * @return a parsed {@link Identifier}
     * @throws IllegalArgumentException if format is invalid
     */
    public static @NotNull Identifier of(@NotNull String value) {
        String[] parts = value.split(":", 2);
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new IllegalArgumentException("Identifier must be in the format 'namespace:path'");
        }
        if (!isValid(parts[0]) || !isValid(parts[1])) {
            throw new IllegalArgumentException("Invalid namespace or path: " + parts[0] + ":" + parts[1]);
        }
        return of(parts[0], parts[1]);
    }

    /**
     * Creates an {@link Identifier} from a namespace and path.
     *
     * @param namespace the namespace
     * @param path the path
     * @return a validated {@link Identifier}
     * @throws IllegalArgumentException if either part is invalid
     */
    public static @NotNull Identifier of(@NotNull String namespace, @NotNull String path) {
        if (!isValid(namespace) || !isValid(path)) {
            throw new IllegalArgumentException("Invalid namespace or path: " + namespace + ":" + path);
        }
        return new Identifier(namespace, path);
    }

    /**
     * Returns the namespace of the Identifier
     *
     * @return the namespace of the Identifier
     */
    public String namespace() {
        return this.namespace;
    }

    /**
     * Returns the path of the Identifier
     *
     * @return the path of the Identifier
     */
    public String path() {
        return this.path;
    }

    /**
     * Returns the string representation {@code namespace:path}.
     */
    @Override
    public @NotNull String toString() {
        return namespace + ":" + path;
    }

    /**
     * Converts this Identifier to a Bukkit {@link NamespacedKey}.
     */
    public @NotNull NamespacedKey toNamespace() {
        return new NamespacedKey(namespace, path);
    }

    /**
     * Converts this Identifier to an Adventure {@link Key}.
     */
    public @NotNull Key toKey() {
        return Key.key(namespace, path);
    }

    public @NotNull ResourceLocation toResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    /**
     * Checks if a string is a valid Minecraft identifier component (only lowercase letters, numbers, underscores, dashes, and periods).
     */
    private static boolean isValid(String input) {
        return input != null && input.matches("[a-z0-9._-]+");
    }
}
