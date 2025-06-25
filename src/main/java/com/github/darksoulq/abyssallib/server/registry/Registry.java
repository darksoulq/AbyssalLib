package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.AbyssalLib;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Core registry that holds mappings from string IDs to objects of type {@code T}.
 * <p>
 * Optionally supports lazy instantiation of entries via a provided factory function.
 * Once objects are registered, the registry behaves as an immutable map externally.
 * Thread-safety is not guaranteed unless externally synchronized.
 *
 * @param <T> the type of object to be registered
 */
public final class Registry<T> {

    /**
     * Internal map of registered entries in insertion order.
     */
    private final Map<String, T> entries = new LinkedHashMap<>();

    /**
     * Optional factory used to create new entries on-demand when {@link #get(String)} is called.
     */
    private final Function<String, T> factory;

    /**
     * Constructs a new Registry with an optional factory function.
     *
     * @param factory a function to generate objects on-demand by ID if not already present;
     *                may be {@code null} to disable lazy creation
     */
    public Registry(Function<String, T> factory) {
        this.factory = factory;
    }

    /**
     * Registers a new object with the given string ID.
     *
     * @param id     the unique string identifier
     * @param object the object to associate with the ID
     * @throws IllegalStateException if the ID is already registered
     */
    public void register(String id, T object) {
        if (entries.containsKey(id)) {
            AbyssalLib.getInstance().getLogger().severe("ID '" + id + "' already registered!");
            throw new IllegalStateException("Duplicate ID registration: " + id);
        }
        entries.put(id, object);
    }

    /**
     * Retrieves an object by its ID. If not found and a factory is defined,
     * the object is created using the factory and automatically registered.
     *
     * @param id the ID of the object
     * @return the registered object, or {@code null} if not found and not creatable
     */
    public T get(String id) {
        T val = entries.get(id);
        if (val == null && factory != null) {
            val = factory.apply(id);
            if (val != null) entries.put(id, val);
        }
        return val;
    }

    /**
     * Checks whether the registry contains the specified ID.
     *
     * @param id the ID to check
     * @return {@code true} if an object with the given ID exists, {@code false} otherwise
     */
    public boolean contains(String id) {
        return entries.containsKey(id);
    }

    /**
     * Returns an unmodifiable view of all registered entries.
     * The returned map preserves insertion order.
     *
     * @return a read-only map of all ID-object pairs
     */
    public Map<String, T> getAll() {
        return Collections.unmodifiableMap(entries);
    }
}
