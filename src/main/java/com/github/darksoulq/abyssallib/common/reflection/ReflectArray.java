package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.reflect.Array;

public final class ReflectArray {

    private ReflectArray() {}

    public static Result<Object> newInstance(Class<?> componentType, int length) {
        try {
            return Result.success(Array.newInstance(componentType, length));
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    public static Result<Object> get(Object array, int index) {
        try {
            return Result.success(Array.get(array, index));
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    public static Result<Void> set(Object array, int index, Object value) {
        try {
            Array.set(array, index, value);
            return Result.success(null);
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    public static Result<Integer> getLength(Object array) {
        try {
            return Result.success(Array.getLength(array));
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }
}