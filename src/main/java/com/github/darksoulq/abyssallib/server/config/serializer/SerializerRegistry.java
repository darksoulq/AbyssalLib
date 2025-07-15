package com.github.darksoulq.abyssallib.server.config.serializer;

import java.util.*;

public class SerializerRegistry {
    private static final Map<Class<?>, ConfigSerializer<?>> global = new HashMap<>();
    public static void register(Class<?> c, ConfigSerializer<?> s) { global.put(c, s); }
    @SuppressWarnings("unchecked")
    public static <T> ConfigSerializer<T> get(Class<T> c) {
        return (ConfigSerializer<T>) global.get(c);
    }
}
