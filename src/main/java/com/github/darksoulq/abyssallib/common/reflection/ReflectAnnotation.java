package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    public Result<Object> value() {
        return getValue("value");
    }
}