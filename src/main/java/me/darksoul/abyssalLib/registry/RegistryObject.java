package me.darksoul.abyssalLib.registry;

import java.util.function.Supplier;

public class RegistryObject<T> implements Supplier<T> {
    private final String id;
    private final Supplier<T> supplier;
    private T cached;

    public RegistryObject(String id, Supplier<T> supplier) {
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
