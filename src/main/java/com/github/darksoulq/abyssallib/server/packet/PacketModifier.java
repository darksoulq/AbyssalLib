package com.github.darksoulq.abyssallib.server.packet;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PacketModifier {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<FieldKey<?, ?>, VarHandle> CACHE = new ConcurrentHashMap<>();

    private PacketModifier() {}

    public static <P, T> T get(P packet, String fieldName, Class<T> type) {
        VarHandle handle = lookupHandle(packet.getClass(), fieldName, type);
        return type.cast(handle.get(packet));
    }

    public static <P, T> void set(P packet, String fieldName, Class<T> type, T value) {
        VarHandle handle = lookupHandle(packet.getClass(), fieldName, type);
        handle.set(packet, value);
    }

    @SuppressWarnings("unchecked")
    private static <P, T> VarHandle lookupHandle(Class<?> clazz, String fieldName, Class<T> fieldType) {
        FieldKey<P, T> key = new FieldKey<>((Class<P>) clazz, fieldName, fieldType);
        return CACHE.computeIfAbsent(key, k -> {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return LOOKUP.unreflectVarHandle(field);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access field '" + fieldName + "' in " + clazz, e);
            }
        });
    }

    private static final class FieldKey<P, T> {
        private final Class<P> clazz;
        private final String fieldName;
        private final Class<T> type;

        private FieldKey(Class<P> clazz, String fieldName, Class<T> type) {
            this.clazz = clazz;
            this.fieldName = fieldName;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof FieldKey<?, ?> k)) return false;
            return clazz == k.clazz && fieldName.equals(k.fieldName) && type == k.type;
        }

        @Override
        public int hashCode() {
            return clazz.hashCode() ^ fieldName.hashCode() ^ type.hashCode();
        }
    }
}
