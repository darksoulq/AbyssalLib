package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

public class ReflectAnnotation<A extends Annotation> {

    private final A annotation;
    private final Class<A> type;

    @SuppressWarnings("unchecked")
    public ReflectAnnotation(A annotation) {
        this.annotation = annotation;
        this.type = (Class<A>) annotation.annotationType();
    }

    public A getUnderlyingAnnotation() {
        return annotation;
    }

    public Class<A> getAnnotationType() {
        return type;
    }

    public Result<Object> getValue(String attribute) {
        try {
            Method method = type.getDeclaredMethod(attribute);
            method.setAccessible(true);
            return Result.success(method.invoke(annotation));
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            return Result.failure(e);
        }
    }

    public Result<Object> value() {
        return getValue("value");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectAnnotation<?> that = (ReflectAnnotation<?>) o;
        return annotation.equals(that.annotation);
    }

    @Override
    public int hashCode() {
        return annotation.hashCode();
    }

    @Override
    public String toString() {
        return annotation.toString();
    }
}