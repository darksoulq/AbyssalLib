package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ReflectConstructor<T> extends ReflectMember<Constructor<T>> {

    private final List<TypeDescriptor> genericParameterTypes;

    protected ReflectConstructor(Constructor<T> constructor) {
        super(constructor);
        this.genericParameterTypes = new ArrayList<>();
        for (java.lang.reflect.Type t : constructor.getGenericParameterTypes()) {
            this.genericParameterTypes.add(new TypeDescriptor(t));
        }
    }

    @Override
    public ReflectConstructor<T> accessible() {
        super.accessible();
        return this;
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

    public Result<T> newInstance(Object... args) {
        try {
            return Result.success(member.newInstance(args));
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            return Result.failure(e);
        }
    }
}