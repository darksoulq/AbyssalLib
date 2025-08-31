package com.github.darksoulq.abyssallib.server.registry.object;

import java.util.function.Supplier;

public class Holder<T> {
    private final Supplier<T> supplier;
    private T cached;

    public Holder(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (cached == null) {
            cached = supplier.get();
        }
        return cached;
    }
}
