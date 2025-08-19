package com.github.darksoulq.abyssallib.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TextUtil {
    /**
     * List of registered Gson type adapters for custom serialization/deserialization.
     */
    private static final List<TypeAdapterRegistration<?>> ADAPTERS = new ArrayList<>();
    /**
     * Gson instance configured with registered type adapters for JSON (de)serialization.
     */
    public static Gson GSON;

    public static void buildGson() {
        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
                .setPrettyPrinting();

        for (TypeAdapterRegistration<?> reg : ADAPTERS) {
            if (reg.hierarchy) {
                builder.registerTypeHierarchyAdapter(reg.clazz, reg.adapter);
            } else {
                builder.registerTypeAdapter(reg.clazz, reg.adapter);
            }
        }

        GSON = builder.create();
    }

    public static <T> void registerTypeAdapter(Class<T> clazz, Object adapter) {
        ADAPTERS.add(new TypeAdapterRegistration<>(clazz, adapter, false));
        buildGson();
    }

    public static <T> void registerTypeHierarchyAdapter(Class<T> clazz, Object adapter) {
        ADAPTERS.add(new TypeAdapterRegistration<>(clazz, adapter, true));
        buildGson();
    }

    private static class TypeAdapterRegistration<T> {
        final Class<T> clazz;
        final Object adapter;
        final boolean hierarchy;

        public TypeAdapterRegistration(Class<T> clazz, Object adapter, boolean hierarchy) {
            this.clazz = clazz;
            this.adapter = adapter;
            this.hierarchy = hierarchy;
        }
    }
}
