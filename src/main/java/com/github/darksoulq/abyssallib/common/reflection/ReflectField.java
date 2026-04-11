package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectField<V> extends ReflectMember<Field> {

    private final MethodHandle getter;
    private final MethodHandle setter;
    private final ReflectType genericType;

    protected ReflectField(Field field) throws IllegalAccessException {
        super(field);
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        this.getter = lookup.unreflectGetter(field);
        this.setter = Modifier.isFinal(modifiers) ? null : lookup.unreflectSetter(field);
        this.genericType = new ReflectType(field.getGenericType());
    }

    public Class<?> getType() {
        return member.getType();
    }

    public ReflectType getGenericType() {
        return genericType;
    }

    @SuppressWarnings("unchecked")
    public Result<V> get(Object instance) {
        try {
            return Result.success((V) (isStatic() ? getter.invokeWithArguments() : getter.invokeWithArguments(instance)));
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    public Result<Void> set(Object instance, V value) {
        if (setter == null) return Result.failure(new IllegalAccessException("Cannot set final field: " + getName()));
        try {
            if (isStatic()) {
                setter.invokeWithArguments(value);
            } else {
                if (instance == null) return Result.failure(new NullPointerException("Instance cannot be null for non-static field: " + getName()));
                setter.invokeWithArguments(instance, value);
            }
            return Result.success(null);
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    @SuppressWarnings("unchecked")
    public <U> ReflectField<U> unchecked() {
        return (ReflectField<U>) this;
    }
}