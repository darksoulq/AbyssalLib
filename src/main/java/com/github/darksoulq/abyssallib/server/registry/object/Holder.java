package com.github.darksoulq.abyssallib.server.registry.object;

import java.util.function.Supplier;

/**
 * A wrapper for a registry object that enables lazy initialization and caching.
 * <p>
 * The {@link Holder} stores a supplier that creates the object. The first time
 * {@link #get()} is called, the supplier is executed and the result is cached
 * for all subsequent calls.
 *
 * @param <T> The type of object held.
 */
public class Holder<T> {
    /** The supplier used to instantiate the object. */
    private final Supplier<T> supplier;
    /** The cached instance of the object. */
    private T cached;

    /**
     * Constructs a new Holder.
     *
     * @param supplier The logic used to create the object when requested.
     */
    public Holder(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Retrieves the held object, creating it if it hasn't been instantiated yet.
     *
     * @return The cached object instance.
     */
    public T get() {
        if (cached == null) {
            cached = supplier.get();
        }
        return cached;
    }
}