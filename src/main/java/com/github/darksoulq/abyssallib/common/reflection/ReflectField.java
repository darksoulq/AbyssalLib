package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.reflect.Field;

public class ReflectField<V> extends ReflectMember<Field> {

    private final TypeDescriptor genericType;

    protected ReflectField(Field field) {
        super(field);
        this.genericType = new TypeDescriptor(field.getGenericType());
    }

    @Override
    public ReflectField<V> accessible() {
        super.accessible();
        return this;
    }

    public Class<?> getType() {
        return member.getType();
    }

    public TypeDescriptor getGenericType() {
        return genericType;
    }

    @SuppressWarnings("unchecked")
    public Result<V> get(Object instance) {
        try {
            return Result.success((V) member.get(instance));
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            return Result.failure(e);
        }
    }

    public Result<Void> set(Object instance, V value) {
        try {
            member.set(instance, value);
            return Result.success(null);
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            return Result.failure(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <U> ReflectField<U> unchecked() {
        return (ReflectField<U>) this;
    }
}