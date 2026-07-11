package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.reflect.Array;

public final class ReflectArray {

    private ReflectArray() {
    }

    public static Result<Object> newInstance(Class<?> componentType, int length) {
        try {
            return Result.success(Array.newInstance(componentType, length));
        } catch (IllegalArgumentException | NegativeArraySizeException e) {
            return Result.failure(e);
        }
    }

    public static Result<Object> get(Object array, int index) {
        try {
            return Result.success(Array.get(array, index));
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            return Result.failure(e);
        }
    }

    public static Result<Void> set(Object array, int index, Object value) {
        try {
            Array.set(array, index, value);
            return Result.success(null);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            return Result.failure(e);
        }
    }

    public static Result<Integer> getLength(Object array) {
        try {
            return Result.success(Array.getLength(array));
        } catch (IllegalArgumentException e) {
            return Result.failure(e);
        }
    }
}