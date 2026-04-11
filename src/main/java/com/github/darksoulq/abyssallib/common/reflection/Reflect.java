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
            if (CLASS_CACHE.containsKey(className)) {
                return Result.success(CLASS_CACHE.get(className));
            }
            Class<?> clazz = Class.forName(className);
            ReflectClass<?> rc = new ReflectClass<>(clazz);
            CLASS_CACHE.put(className, rc);
            return Result.success(rc);
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }
}