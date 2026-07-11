package com.github.darksoulq.abyssallib.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.NoSuchElementException;

public abstract class ReflectMember<M extends AccessibleObject & Member> {

    protected final M member;
    protected final int modifiers;

    protected ReflectMember(M member) {
        this.member = member;
        this.modifiers = member.getModifiers();
    }

    public ReflectMember<M> accessible() {
        this.member.setAccessible(true);
        return this;
    }

    public M getUnderlyingMember() {
        return member;
    }

    public String getName() {
        return member.getName();
    }

    public Class<?> getDeclaringClass() {
        return member.getDeclaringClass();
    }

    public int getModifiers() {
        return modifiers;
    }

    public boolean isPublic() {
        return Modifier.isPublic(modifiers);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(modifiers);
    }

    public boolean isProtected() {
        return Modifier.isProtected(modifiers);
    }

    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(modifiers);
    }

    public boolean isTransient() {
        return Modifier.isTransient(modifiers);
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(modifiers);
    }

    public boolean isSynchronized() {
        return Modifier.isSynchronized(modifiers);
    }

    public boolean isNative() {
        return Modifier.isNative(modifiers);
    }

    public boolean isStrict() {
        return Modifier.isStrict(modifiers);
    }

    public <A extends Annotation> Result<A> getAnnotation(Class<A> annotationClass) {
        A annotation = member.getAnnotation(annotationClass);
        if (annotation == null) {
            return Result.failure(new NoSuchElementException("Annotation not found: " + annotationClass.getName()));
        }
        return Result.success(annotation);
    }

    public <A extends Annotation> Result<ReflectAnnotation<A>> getReflectAnnotation(Class<A> annotationClass) {
        return getAnnotation(annotationClass).map(ReflectAnnotation::new);
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return member.isAnnotationPresent(annotationClass);
    }

    public Annotation[] getAnnotations() {
        return member.getAnnotations();
    }

    public Annotation[] getDeclaredAnnotations() {
        return member.getDeclaredAnnotations();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectMember<?> that = (ReflectMember<?>) o;
        return member.equals(that.member);
    }

    @Override
    public int hashCode() {
        return member.hashCode();
    }

    @Override
    public String toString() {
        return member.toString();
    }
}