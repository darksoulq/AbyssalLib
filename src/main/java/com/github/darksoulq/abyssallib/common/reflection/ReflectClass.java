package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ReflectClass<T> {

    private final Class<T> clazz;
    private final int modifiers;

    private final Map<String, ReflectField<?>> fieldCache = new ConcurrentHashMap<>();
    private final Map<MethodKey, ReflectMethod<?>> methodCache = new ConcurrentHashMap<>();
    private final Map<ConstructorKey, ReflectConstructor<T>> constructorCache = new ConcurrentHashMap<>();

    private record MethodKey(String name, List<Class<?>> paramTypes) {}
    private record ConstructorKey(List<Class<?>> paramTypes) {}

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

    public List<ReflectClass<?>> getInterfaces() {
        return Arrays.stream(clazz.getInterfaces())
            .map(Reflect::of)
            .collect(Collectors.toList());
    }

    public List<ReflectField<?>> fields() {
        return Arrays.stream(clazz.getDeclaredFields())
            .map(ReflectField::new)
            .collect(Collectors.toList());
    }

    public List<ReflectMethod<?>> methods() {
        return Arrays.stream(clazz.getDeclaredMethods())
            .map(ReflectMethod::new)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<ReflectConstructor<T>> constructors() {
        return Arrays.stream(clazz.getDeclaredConstructors())
            .map(c -> new ReflectConstructor<>((Constructor<T>) c))
            .collect(Collectors.toList());
    }

    public List<ReflectAnnotation<?>> annotations() {
        return Arrays.stream(clazz.getAnnotations())
            .map(ReflectAnnotation::new)
            .collect(Collectors.toList());
    }

    public List<ReflectMethod<?>> recordComponents() {
        if (!isRecord()) return new ArrayList<>();
        return Arrays.stream(clazz.getRecordComponents())
            .map(RecordComponent::getAccessor)
            .map(ReflectMethod::new)
            .collect(Collectors.toList());
    }

    public <A extends Annotation> Result<A> getAnnotation(Class<A> annotationClass) {
        A annotation = clazz.getAnnotation(annotationClass);
        if (annotation == null) {
            return Result.failure(new NoSuchElementException("Annotation not found: " + annotationClass.getName()));
        }
        return Result.success(annotation);
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
            return Result.success((ReflectField<V>) fieldCache.computeIfAbsent(name, k -> {
                try {
                    return new ReflectField<>(findField(clazz, k));
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (RuntimeException e) {
            return Result.failure(e.getCause() != null ? e.getCause() : e);
        }
    }

    public <V> Result<ReflectField<V>> field(String name, Class<V> expectedType) {
        return this.<V>field(name).flatMap(f -> {
            if (!expectedType.isAssignableFrom(f.getType())) {
                return Result.failure(new ClassCastException("Field " + name + " type " + f.getType().getName() + " is not assignable to " + expectedType.getName()));
            }
            return Result.success(f);
        });
    }

    public <V> List<ReflectField<V>> fieldsByType(Class<V> type) {
        List<ReflectField<V>> found = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field f : current.getDeclaredFields()) {
                if (type.isAssignableFrom(f.getType())) {
                    found.add(new ReflectField<>(f).unchecked());
                }
            }
            current = current.getSuperclass();
        }
        return found;
    }

    public <V> Result<ReflectField<V>> fieldByType(Class<V> type) {
        List<ReflectField<V>> found = fieldsByType(type);
        if (found.isEmpty()) {
            return Result.failure(new NoSuchFieldException("No field of type " + type.getName() + " found in " + clazz.getName()));
        }
        if (found.size() > 1) {
            return Result.failure(new IllegalStateException("Ambiguous field resolution: found " + found.size() + " fields assignable to " + type.getName()));
        }
        return Result.success(found.getFirst());
    }

    @SuppressWarnings("unchecked")
    public <R> Result<ReflectMethod<R>> method(String name, Class<?>... paramTypes) {
        MethodKey key = new MethodKey(name, Arrays.asList(paramTypes));
        try {
            return Result.success((ReflectMethod<R>) methodCache.computeIfAbsent(key, k -> {
                try {
                    return new ReflectMethod<>(findMethod(clazz, k.name(), k.paramTypes().toArray(new Class<?>[0])));
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (RuntimeException e) {
            return Result.failure(e.getCause() != null ? e.getCause() : e);
        }
    }

    public <R> Result<ReflectMethod<R>> method(String name, Class<R> expectedReturnType, Class<?>... paramTypes) {
        return this.<R>method(name, paramTypes).flatMap(m -> {
            if (!expectedReturnType.isAssignableFrom(m.getReturnType())) {
                return Result.failure(new ClassCastException("Method " + name + " return type " + m.getReturnType().getName() + " is not assignable to " + expectedReturnType.getName()));
            }
            return Result.success(m);
        });
    }

    public Result<ReflectConstructor<T>> constructor(Class<?>... paramTypes) {
        ConstructorKey key = new ConstructorKey(Arrays.asList(paramTypes));
        try {
            return Result.success(constructorCache.computeIfAbsent(key, k -> {
                try {
                    return new ReflectConstructor<>(findConstructor(clazz, k.paramTypes().toArray(new Class<?>[0])));
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (RuntimeException e) {
            return Result.failure(e.getCause() != null ? e.getCause() : e);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectClass<?> that = (ReflectClass<?>) o;
        return clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }

    @Override
    public String toString() {
        return clazz.getName();
    }
}