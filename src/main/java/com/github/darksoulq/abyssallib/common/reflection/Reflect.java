package com.github.darksoulq.abyssallib.common.reflection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Reflect {

    private static final Map<String, ReflectClass<?>> CLASS_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, ReflectClass<?>> TYPE_CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> ReflectClass<T> of(Class<T> clazz) {
        return (ReflectClass<T>) TYPE_CACHE.computeIfAbsent(clazz, ReflectClass::new);
    }

    public static Result<ReflectClass<?>> of(String className) {
        try {
            return Result.success(CLASS_CACHE.computeIfAbsent(className, k -> {
                try {
                    return new ReflectClass<>(Class.forName(k));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ReflectiveOperationException) {
                return Result.failure(e.getCause());
            }
            return Result.failure(e);
        }
    }
}