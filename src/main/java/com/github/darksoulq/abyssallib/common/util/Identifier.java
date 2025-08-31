package com.github.darksoulq.abyssallib.common.util;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import net.kyori.adventure.key.Key;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a resource identifier in the format {@code namespace:path}.
 */
public class Identifier {
    public static final Codec<Identifier> CODEC = Codecs.STRING.xmap(Identifier::of, Identifier::toString);

    private final String key;
    private final String namespace;
    private final String path;

    private Identifier(String key, String namespace, String path) {
        this.key = key;
        this.namespace = namespace;
        this.path = path;
    }

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

    public static @NotNull Identifier of(@NotNull String namespace, @NotNull String path) {
        if (!isValid(namespace) || !isValid(path)) {
            throw new IllegalArgumentException("Invalid namespace or path: " + namespace + ":" + path);
        }
        return new Identifier(null, namespace, path);
    }

    public static @NotNull Identifier of(@NotNull String key, @NotNull String namespace, @NotNull String path) {
        if (!isValid(key) || !isValid(namespace) || !isValid(path)) {
            throw new IllegalArgumentException("Invalid key/namespace/path: " + key + ":" + namespace + ":" + path);
        }
        return new Identifier(key, namespace, path);
    }

    public String key() {
        return this.key;
    }
    public String namespace() {
        return this.namespace;
    }
    public String path() {
        return this.path;
    }

    @Override
    public @NotNull String toString() {
        return key != null ? key + ":" + namespace + ":" + path : namespace + ":" + path;
    }

    public @NotNull NamespacedKey toNamespace() {
        return new NamespacedKey(namespace, path);
    }
    public @NotNull Key toKey() {
        return Key.key(namespace, path);
    }
    public @NotNull ResourceLocation toResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static boolean isValid(String input) {
        return input != null && input.matches("[a-z0-9._-]+");
    }
    public static boolean isValid2Part(String input) {
        if (input == null) return false;
        String[] parts = input.split(":", 2);
        return parts.length == 2 && isValid(parts[0]) && isValid(parts[1]);
    }
    public static boolean isValid3Part(String input) {
        if (input == null) return false;
        String[] parts = input.split(":", 3);
        return parts.length == 3 && isValid(parts[0]) && isValid(parts[1]) && isValid(parts[2]);
    }
}
