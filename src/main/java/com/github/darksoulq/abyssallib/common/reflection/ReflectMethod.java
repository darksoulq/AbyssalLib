package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectMethod<R> extends ReflectMember<Method> {

    private final TypeDescriptor genericReturnType;
    private final List<TypeDescriptor> genericParameterTypes;

    protected ReflectMethod(Method method) {
        super(method);
        this.genericReturnType = new TypeDescriptor(method.getGenericReturnType());
        this.genericParameterTypes = new ArrayList<>();
        for (java.lang.reflect.Type t : method.getGenericParameterTypes()) {
            this.genericParameterTypes.add(new TypeDescriptor(t));
        }
    }

    @Override
    public ReflectMethod<R> accessible() {
        super.accessible();
        return this;
    }

    public Class<?> getReturnType() {
        return member.getReturnType();
    }

    public TypeDescriptor getGenericReturnType() {
        return genericReturnType;
    }

    public Class<?>[] getParameterTypes() {
        return member.getParameterTypes();
    }

    public List<TypeDescriptor> getGenericParameterTypes() {
        return new ArrayList<>(genericParameterTypes);
    }

    public int getParameterCount() {
        return member.getParameterCount();
    }

    @SuppressWarnings("unchecked")
    public Result<R> invoke(Object instance, Object... args) {
        try {
            return Result.success((R) member.invoke(instance, args));
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            return Result.failure(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <U> ReflectMethod<U> unchecked() {
        return (ReflectMethod<U>) this;
    }
}