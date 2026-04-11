package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class ReflectMethod<R> extends ReflectMember<Method> {

    private final MethodHandle handle;
    private final ReflectType genericReturnType;
    private final ReflectType[] genericParameterTypes;

    protected ReflectMethod(Method method) throws IllegalAccessException {
        super(method);
        this.handle = MethodHandles.lookup().unreflect(method);
        this.genericReturnType = new ReflectType(method.getGenericReturnType());

        java.lang.reflect.Type[] gParamTypes = method.getGenericParameterTypes();
        this.genericParameterTypes = new ReflectType[gParamTypes.length];
        for (int i = 0; i < gParamTypes.length; i++) {
            this.genericParameterTypes[i] = new ReflectType(gParamTypes[i]);
        }
    }

    public Class<?> getReturnType() {
        return member.getReturnType();
    }

    public ReflectType getGenericReturnType() {
        return genericReturnType;
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
    public Result<R> invoke(Object instance, Object... args) {
        try {
            Object[] finalArgs;
            if (isStatic()) {
                finalArgs = args;
            } else {
                if (instance == null) return Result.failure(new NullPointerException("Instance cannot be null for non-static method: " + getName()));
                finalArgs = new Object[args.length + 1];
                finalArgs[0] = instance;
                System.arraycopy(args, 0, finalArgs, 1, args.length);
            }
            return Result.success((R) handle.invokeWithArguments(finalArgs));
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    @SuppressWarnings("unchecked")
    public <U> ReflectMethod<U> unchecked() {
        return (ReflectMethod<U>) this;
    }
}