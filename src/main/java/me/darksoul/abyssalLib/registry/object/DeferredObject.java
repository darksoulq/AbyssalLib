package me.darksoul.abyssalLib.registry.object;

import java.util.function.Supplier;

public class DeferredObject<T> implements Supplier<T> {
    private final String id;
    private final Supplier<T> supplier;
    private T cached;

    public DeferredObject(String id, Supplier<T> supplier) {
        this.id = id;
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (cached == null) {
            cached = supplier.get();
        }
        return cached;
    }

    public String getId() {
        return id;
    }
}
