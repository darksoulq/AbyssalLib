package com.github.darksoulq.abyssallib.common.util;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import net.kyori.adventure.key.Key;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a resource identifier in the format {@code namespace:path} or {@code key:namespace:path}.
 */
public class Identifier {
    /**
     * Codec for serialization/deserialization of Identifier
     */
    public static final Codec<Identifier> CODEC = Codecs.STRING.xmap(Identifier::of, Identifier::toString);

    private final String key;
    private final String namespace;
    private final String path;

    /**
     * Constructs a new Identifier.
     *
     * @param key the key of the Identifier
     * @param namespace the namespace of the Identifier
     * @param path the path of the Identifier
     */
    private Identifier(String key, String namespace, String path) {
        this.key = key;
        this.namespace = namespace;
        this.path = path;
    }

    /**
     * Constructs a new Identifier from the given string
     * The string can be in the format {@code namespace:path} or {@code key:namespace:path}
     *
     * @param value the string value
     * @return new Identifier from the string
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
     * Constructs a new Identifier with the provided namespace and path
     *
     * @param namespace the namespace
     * @param path the path
     * @return new Identifier from the namespace and path
     */
    public static @NotNull Identifier of(@NotNull String namespace, @NotNull String path) {
        if (!isValid(namespace) || !isValid(path)) {
            throw new IllegalArgumentException("Invalid namespace or path: " + namespace + ":" + path);
        }
        return new Identifier(null, namespace, path);
    }

    /**
     * Constructs a new Identifier with the provided key, namespace and path
     *
     * @param key the key
     * @param namespace the namespace
     * @param path the path
     * @return new Identifier from the key, namespace and path
     */
    public static @NotNull Identifier of(@NotNull String key, @NotNull String namespace, @NotNull String path) {
        if (!isValid(key) || !isValid(namespace) || !isValid(path)) {
            throw new IllegalArgumentException("Invalid key/namespace/path: " + key + ":" + namespace + ":" + path);
        }
        return new Identifier(key, namespace, path);
    }

    /**
     * Gets the key of the Identifier (or null if it doesn't exist).
     *
     * @return The key.
     */
    public String getKey() {
        return this.key;
    }
    /**
     * Gets the namespace of the Identifier.
     *
     * @return The namespace.
     */
    public String getNamespace() {
        return this.namespace;
    }
    /**
     * Gets the path of the Identifier.
     *
     * @return The path.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Gets string representation of the identifier ({@code namespace:path} or {@code key:namespace:path}
     *
     * @return The string representation.
     */
    @Override
    public @NotNull String toString() {
        return key != null ? key + ":" + namespace + ":" + path : namespace + ":" + path;
    }

    /**
     * Gets the Identifier as a {@link NamespacedKey}
     * Ignores the {@code key}
     *
     * @return the NamespacedKey
     */
    public @NotNull NamespacedKey asNamespacedKey() {
        return new NamespacedKey(namespace, path);
    }
    /**
     * Gets the Identifier as a {@link Key}
     * Ignores the {@code key}
     *
     * @return the Key
     */
    public @NotNull Key asKey() {
        return Key.key(namespace, path);
    }
    /**
     * Gets the Identifier as a {@link ResourceLocation}
     * Ignores the {@code key}
     *
     * @return the ResourceLocation
     */
    public @NotNull ResourceLocation asResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    /**
     * Checks if the given string is a valid identifier.
     *
     * @param input the string
     * @return Whether it's a valid identifier or not
     */
    public static boolean isValid(String input) {
        return input != null && input.matches("[a-z0-9._-]+");
    }
    /**
     * Checks if the given string is a valid 2-part identifier {@code namespace:path}.
     *
     * @param input the string
     * @return Whether it's a valid 2-part identifier or not
     */
    public static boolean isValid2Part(String input) {
        if (input == null) return false;
        String[] parts = input.split(":", 2);
        return parts.length == 2 && isValid(parts[0]) && isValid(parts[1]);
    }
    /**
     * Checks if the given string is a valid 3-part identifier {@code key:namespace:path}.
     *
     * @param input the string
     * @return Whether it's a valid 3-part identifier or not
     */
    public static boolean isValid3Part(String input) {
        if (input == null) return false;
        String[] parts = input.split(":", 3);
        return parts.length == 3 && isValid(parts[0]) && isValid(parts[1]) && isValid(parts[2]);
    }
}
