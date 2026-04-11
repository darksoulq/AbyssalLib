package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

public class ReflectType {

    private final Type type;

    public ReflectType(Type type) {
        this.type = type;
    }

    public Type getUnderlyingType() {
        return type;
    }

    public boolean isClass() { return type instanceof Class<?>; }
    public boolean isParameterized() { return type instanceof ParameterizedType; }
    public boolean isWildcard() { return type instanceof WildcardType; }
    public boolean isGenericArray() { return type instanceof GenericArrayType; }

    public Result<ReflectClass<?>> getRawType() {
        if (type instanceof ParameterizedType pt) {
            return Result.success(Reflect.of((Class<?>) pt.getRawType()));
        } else if (type instanceof Class<?> c) {
            return Result.success(Reflect.of(c));
        } else if (type instanceof GenericArrayType gat) {
            Type compType = gat.getGenericComponentType();
            if (compType instanceof Class<?> c) {
                return Result.success(Reflect.of(c.arrayType()));
            }
        }
        return Result.failure(new IllegalStateException("Cannot resolve raw type for " + type.getTypeName()));
    }

    public List<ReflectType> getTypeArguments() {
        List<ReflectType> args = new ArrayList<>();
        if (type instanceof ParameterizedType pt) {
            for (Type t : pt.getActualTypeArguments()) {
                args.add(new ReflectType(t));
            }
        }
        return args;
    }

    public Result<ReflectType> getArrayComponentType() {
        if (type instanceof GenericArrayType gat) {
            return Result.success(new ReflectType(gat.getGenericComponentType()));
        } else if (type instanceof Class<?> c && c.isArray()) {
            return Result.success(new ReflectType(c.getComponentType()));
        }
        return Result.failure(new IllegalStateException("Not an array type"));
    }
}