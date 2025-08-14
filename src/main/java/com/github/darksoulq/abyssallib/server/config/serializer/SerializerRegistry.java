package com.github.darksoulq.abyssallib.server.config.serializer;

import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class SerializerRegistry {
    private static final Map<Class<?>, Serializer<?>> serializers = new ConcurrentHashMap<>();

    static {
        register(UUID.class, new BuiltinSerializers.UUIDSerializer());
        register(ItemStack.class, new BuiltinSerializers.ItemStackSerializer());
    }

    public static <T> void register(Class<T> type, Serializer<T> serializer) {
        serializers.put(type, serializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> get(Class<T> type) {
        return (Serializer<T>) serializers.get(type);
    }

    public static boolean has(Class<?> type) {
        return serializers.containsKey(type);
    }
}
