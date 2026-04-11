package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

public class ReflectConstructor<T> extends ReflectMember<Constructor<T>> {

    private final MethodHandle handle;
    private final ReflectType[] genericParameterTypes;

    protected ReflectConstructor(Constructor<T> constructor) throws IllegalAccessException {
        super(constructor);
        this.handle = MethodHandles.lookup().unreflectConstructor(constructor);
        
        java.lang.reflect.Type[] gParamTypes = constructor.getGenericParameterTypes();
        this.genericParameterTypes = new ReflectType[gParamTypes.length];
        for (int i = 0; i < gParamTypes.length; i++) {
            this.genericParameterTypes[i] = new ReflectType(gParamTypes[i]);
        }
    }

    public Class<?>[] getParameterTypes() {
        return member.getParameterTypes();
    }

    public ReflectType[] getGenericParameterTypes() {
        return genericParameterTypes.clone();
    }

    public int getParameterCount() {
        return member.getParameterCount();
    }

    @SuppressWarnings("unchecked")
    public Result<T> newInstance(Object... args) {
        try {
            return Result.success((T) handle.invokeWithArguments(args));
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }
}