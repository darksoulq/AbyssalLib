package me.darksoul.abyssalLib.util;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;

public record ResourceLocation(String namespace, String path) {
    public String path() {
        return path;
    }
    public String namespace() {
        return namespace;
    }
    @Override
    public String toString() {
        return namespace + ":" + path;
    }

    public static ResourceLocation fromString(String str) {
        String[] np = str.split(":", 2);
        return new ResourceLocation(np[0], np[1]);
    }
    public NamespacedKey toNamespace() {
        return new NamespacedKey(namespace, path);
    }
    public Key toKey() {
        return Key.key(namespace + ":" + path);
    }
}
