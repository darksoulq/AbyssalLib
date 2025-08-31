package com.github.darksoulq.abyssallib.common.util;

public final class ClassContainer<T> {
    private final Class<T> clazz;

    public ClassContainer(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
