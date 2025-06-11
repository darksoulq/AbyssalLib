package com.github.darksoulq.abyssallib.server.registry.object;

import java.util.function.Supplier;

/**
 * A lazily-initialized object holder used in deferred registration.
 *
 * <p>Instances of this class wrap a {@link Supplier} for an object of type {@code T},
 * and only create the object the first time {@link #get()} is called. The result is
 * cached and returned on all future calls.</p>
 *
 * @param <T> the type of the object being supplied
 */
public class DeferredObject<T> implements Supplier<T> {
    private final String id;
    private final Supplier<T> supplier;
    private T cached;

    /**
     * Constructs a new {@link DeferredObject}.
     *
     * @param id the full namespaced ID of the object (e.g., "modid:name")
     * @param supplier the supplier used to construct the object when needed
     */
    public DeferredObject(String id, Supplier<T> supplier) {
        this.id = id;
        this.supplier = supplier;
    }

    /**
     * Returns the object, creating and caching it on first access.
     *
     * @return the supplied and cached object
     */
    @Override
    public T get() {
        if (cached == null) {
            cached = supplier.get();
        }
        return cached;
    }

    /**
     * Returns the ID associated with this object.
     *
     * @return the object's ID
     */
    public String getId() {
        return id;
    }
}
