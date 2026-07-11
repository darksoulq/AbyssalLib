package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class TypeDescriptor {

    private final Type type;

    public TypeDescriptor(Type type) {
        this.type = type;
    }

    public Type getUnderlyingType() {
        return type;
    }

    public boolean isClass() {
        return type instanceof Class<?>;
    }

    public boolean isParameterized() {
        return type instanceof ParameterizedType;
    }

    public boolean isWildcard() {
        return type instanceof WildcardType;
    }

    public boolean isGenericArray() {
        return type instanceof GenericArrayType;
    }

    public boolean isTypeVariable() {
        return type instanceof TypeVariable<?>;
    }

    public Result<ReflectClass<?>> rawType() {
        if (type instanceof ParameterizedType pt) {
            return Result.success(Reflect.of((Class<?>) pt.getRawType()));
        } else if (type instanceof Class<?> c) {
            return Result.success(Reflect.of(c));
        } else if (type instanceof GenericArrayType gat) {
            return new TypeDescriptor(gat.getGenericComponentType()).rawType()
                .map(rc -> Reflect.of(rc.getUnderlyingClass().arrayType()));
        } else if (type instanceof TypeVariable<?> tv) {
            Type[] bounds = tv.getBounds();
            if (bounds.length > 0) {
                return new TypeDescriptor(bounds[0]).rawType();
            }
            return Result.success(Reflect.of(Object.class));
        } else if (type instanceof WildcardType wt) {
            Type[] upperBounds = wt.getUpperBounds();
            if (upperBounds.length > 0) {
                return new TypeDescriptor(upperBounds[0]).rawType();
            }
            return Result.success(Reflect.of(Object.class));
        }
        return Result.failure(new IllegalStateException("Cannot resolve raw type for " + type.getTypeName()));
    }

    public List<TypeDescriptor> getTypeArguments() {
        List<TypeDescriptor> args = new ArrayList<>();
        if (type instanceof ParameterizedType pt) {
            for (Type t : pt.getActualTypeArguments()) {
                args.add(new TypeDescriptor(t));
            }
        }
        return args;
    }

    public Result<TypeDescriptor> typeArgument(int index) {
        List<TypeDescriptor> args = getTypeArguments();
        if (index >= 0 && index < args.size()) {
            return Result.success(args.get(index));
        }
        return Result.failure(new IndexOutOfBoundsException("Invalid type argument index: " + index));
    }

    public List<TypeDescriptor> upperBounds() {
        List<TypeDescriptor> bounds = new ArrayList<>();
        if (type instanceof WildcardType wt) {
            for (Type t : wt.getUpperBounds()) {
                bounds.add(new TypeDescriptor(t));
            }
        } else if (type instanceof TypeVariable<?> tv) {
            for (Type t : tv.getBounds()) {
                bounds.add(new TypeDescriptor(t));
            }
        }
        return bounds;
    }

    public List<TypeDescriptor> lowerBounds() {
        List<TypeDescriptor> bounds = new ArrayList<>();
        if (type instanceof WildcardType wt) {
            for (Type t : wt.getLowerBounds()) {
                bounds.add(new TypeDescriptor(t));
            }
        }
        return bounds;
    }

    public Result<TypeDescriptor> getArrayComponentType() {
        if (type instanceof GenericArrayType gat) {
            return Result.success(new TypeDescriptor(gat.getGenericComponentType()));
        } else if (type instanceof Class<?> c && c.isArray()) {
            return Result.success(new TypeDescriptor(c.getComponentType()));
        }
        return Result.failure(new IllegalStateException("Not an array type"));
    }

    public boolean isAssignableTo(Class<?> target) {
        return rawType()
            .map(rc -> target.isAssignableFrom(rc.getUnderlyingClass()))
            .getOrElse(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDescriptor that = (TypeDescriptor) o;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return type.getTypeName();
    }
}