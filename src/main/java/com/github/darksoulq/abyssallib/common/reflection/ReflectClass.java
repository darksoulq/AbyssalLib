package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectClass<T> {

    private final Class<T> clazz;
    private final int modifiers;

    private final Map<String, ReflectField<?>> fieldCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, ReflectField<?>> fieldByTypeCache = new ConcurrentHashMap<>();
    private final Map<String, ReflectMethod<?>> methodCache = new ConcurrentHashMap<>();
    private final Map<Integer, ReflectConstructor<T>> constructorCache = new ConcurrentHashMap<>();

    protected ReflectClass(Class<T> clazz) {
        this.clazz = clazz;
        this.modifiers = clazz.getModifiers();
    }

    public Class<T> getUnderlyingClass() {
        return clazz;
    }

    public String getName() {
        return clazz.getName();
    }

    public String getSimpleName() {
        return clazz.getSimpleName();
    }

    public boolean isPublic() { return Modifier.isPublic(modifiers); }
    public boolean isPrivate() { return Modifier.isPrivate(modifiers); }
    public boolean isProtected() { return Modifier.isProtected(modifiers); }
    public boolean isStatic() { return Modifier.isStatic(modifiers); }
    public boolean isFinal() { return Modifier.isFinal(modifiers); }
    public boolean isAbstract() { return Modifier.isAbstract(modifiers); }
    public boolean isInterface() { return Modifier.isInterface(modifiers); }
    public boolean isEnum() { return clazz.isEnum(); }
    public boolean isArray() { return clazz.isArray(); }
    public boolean isAnnotation() { return clazz.isAnnotation(); }
    public boolean isRecord() { return clazz.isRecord(); }

    public Result<ReflectClass<?>> getSuperclass() {
        Class<?> superclass = clazz.getSuperclass();
        if (superclass == null) {
            return Result.failure(new NoSuchFieldException("Class " + clazz.getName() + " has no superclass"));
        }
        return Result.success(Reflect.of(superclass));
    }

    public Result<ReflectClass<?>> getComponentType() {
        if (isArray()) return Result.success(Reflect.of(clazz.getComponentType()));
        return Result.failure(new IllegalStateException(clazz.getName() + " is not an array"));
    }

    public Result<T[]> getEnumConstants() {
        if (isEnum()) return Result.success(clazz.getEnumConstants());
        return Result.failure(new IllegalStateException(clazz.getName() + " is not an enum"));
    }

    public ReflectClass<?>[] getInterfaces() {
        Class<?>[] interfaces = clazz.getInterfaces();
        ReflectClass<?>[] reflectInterfaces = new ReflectClass<?>[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            reflectInterfaces[i] = Reflect.of(interfaces[i]);
        }
        return reflectInterfaces;
    }

    public <A extends Annotation> Result<A> getAnnotation(Class<A> annotationClass) {
        A annotation = clazz.getAnnotation(annotationClass);
        return annotation != null ? Result.success(annotation) : Result.failure(new NullPointerException("Annotation not found"));
    }

    public <A extends Annotation> Result<ReflectAnnotation<A>> getReflectAnnotation(Class<A> annotationClass) {
        return getAnnotation(annotationClass).map(ReflectAnnotation::new);
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return clazz.isAnnotationPresent(annotationClass);
    }

    public Annotation[] getAnnotations() {
        return clazz.getAnnotations();
    }

    public Annotation[] getDeclaredAnnotations() {
        return clazz.getDeclaredAnnotations();
    }

    @SuppressWarnings("unchecked")
    public <V> Result<ReflectField<V>> field(String name) {
        try {
            if (fieldCache.containsKey(name)) {
                return Result.success((ReflectField<V>) fieldCache.get(name));
            }
            Field f = findField(clazz, name);
            ReflectField<V> rf = new ReflectField<>(f);
            fieldCache.put(name, rf);
            return Result.success(rf);
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    @SuppressWarnings("unchecked")
    public <V> Result<ReflectField<V>> field(String name, Class<V> expectedType) {
        return field(name);
    }

    @SuppressWarnings("unchecked")
    public <V> Result<ReflectField<V>> fieldByType(Class<V> type) {
        try {
            if (fieldByTypeCache.containsKey(type)) {
                return Result.success((ReflectField<V>) fieldByTypeCache.get(type));
            }

            for (Field f : clazz.getDeclaredFields()) {
                if (f.getType() == type) {
                    ReflectField<V> rf = new ReflectField<>(f);
                    fieldByTypeCache.put(type, rf);
                    return Result.success(rf);
                }
            }

            Class<?> current = clazz.getSuperclass();
            while (current != null && current != Object.class) {
                for (Field f : current.getDeclaredFields()) {
                    if (f.getType() == type) {
                        ReflectField<V> rf = new ReflectField<>(f);
                        fieldByTypeCache.put(type, rf);
                        return Result.success(rf);
                    }
                }
                current = current.getSuperclass();
            }

            return Result.failure(new NoSuchFieldException("Field of type " + type.getName() + " not found in " + clazz.getName()));
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    @SuppressWarnings("unchecked")
    public <R> Result<ReflectMethod<R>> method(String name, Class<?>... paramTypes) {
        try {
            String key = name + Arrays.hashCode(paramTypes);
            if (methodCache.containsKey(key)) {
                return Result.success((ReflectMethod<R>) methodCache.get(key));
            }
            Method m = findMethod(clazz, name, paramTypes);
            ReflectMethod<R> rm = new ReflectMethod<>(m);
            methodCache.put(key, rm);
            return Result.success(rm);
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    @SuppressWarnings("unchecked")
    public <R> Result<ReflectMethod<R>> method(String name, Class<R> expectedReturnType, Class<?>... paramTypes) {
        return method(name, paramTypes);
    }

    public Result<ReflectConstructor<T>> constructor(Class<?>... paramTypes) {
        try {
            int key = Arrays.hashCode(paramTypes);
            if (constructorCache.containsKey(key)) {
                return Result.success(constructorCache.get(key));
            }
            Constructor<T> c = findConstructor(clazz, paramTypes);
            ReflectConstructor<T> rc = new ReflectConstructor<>(c);
            constructorCache.put(key, rc);
            return Result.success(rc);
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    private Field findField(Class<?> current, String name) throws NoSuchFieldException {
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found in " + clazz.getName());
    }

    private Method findMethod(Class<?> current, String name, Class<?>... paramTypes) throws NoSuchMethodException {
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredMethod(name, paramTypes);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method " + name + " not found in " + clazz.getName());
    }

    private Constructor<T> findConstructor(Class<T> current, Class<?>... paramTypes) throws NoSuchMethodException {
        return current.getDeclaredConstructor(paramTypes);
    }
}