package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.AbyssalLib;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Core registry that holds mappings from ID -> T
 * Immutable-ish after registrations, thread-safe if used properly.
 */
public final class Registry<T> {
    private final Map<String, T> entries = new LinkedHashMap<>();
    private final Function<String, T> factory;

    /**
     * Creates a registry with a factory to create objects if needed.
     * @param factory optional function to create object by ID (can be null)
     */
    public Registry(Function<String, T> factory) {
        this.factory = factory;
    }

    /**
     * Registers a new object with given ID.
     * @throws IllegalStateException if already registered.
     */
    public void register(String id, T object) {
        if (entries.containsKey(id)) {
            AbyssalLib.getInstance().getLogger().severe("ID '" + id + "' already registered!");
            throw new IllegalStateException("Duplicate ID registration: " + id);
        }
        entries.put(id, object);
    }

    /**
     * Gets an object by ID or creates it via factory if present.
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
     * Checks if registry contains ID.
     */
    public boolean contains(String id) {
        return entries.containsKey(id);
    }

    /**
     * Returns unmodifiable view of all registered entries.
     */
    public Map<String, T> getAll() {
        return Collections.unmodifiableMap(entries);
    }
}
