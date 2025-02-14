package me.darksoul.abyssalLib.util;

import org.bukkit.NamespacedKey;

public class StringUtils {
    public static NamespacedKey toNamespacedKey(String string) {
        String[] namespaceKey = string.split(":", 2);
        return new NamespacedKey(namespaceKey[0], namespaceKey[1]);
    }
}