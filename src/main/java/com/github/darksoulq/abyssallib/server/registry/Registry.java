package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.Map;

/**
 * A generic registry system for managing library objects and their associated unique identifiers.
 * <p>
 * The registry uses a {@link BiMap} to ensure that every object has exactly one ID and every ID
 * points to exactly one object. This is essential for consistent serialization and lookup
 * across the library.
 *
 * @param <T> The type of object being registered.
 */
public final class Registry<T> {

    /** The internal bidirectional map storing ID-to-Object relationships. */
    private final BiMap<String, T> entries = HashBiMap.create();

    /**
     * Registers a new object with a unique identifier.
     * <p>
     * If the ID is already present in the registry, the registration is rejected
     * and a severe error is logged to the console to prevent accidental overwrites.
     *
     * @param id     The unique string identifier for the object.
     * @param object The object instance to register.
     */
    public void register(String id, T object) {
        if (entries.containsKey(id)) {
            AbyssalLib.getInstance().getLogger().severe("ID '" + id + "' already registered! Skipping...");
            return;
        }
        entries.put(id, object);
    }

    /**
     * Retrieves an object from the registry by its identifier.
     *
     * @param id The unique ID of the object.
     * @return The registered object, or {@code null} if no object is mapped to the ID.
     */
    public T get(String id) {
        return entries.get(id);
    }

    /**
     * Retrieves the unique identifier associated with a registered object instance.
     *
     * @param value The object instance to look up.
     * @return The string ID associated with the object, or {@code null} if the object is not registered.
     */
    public String getId(T value) {
        return entries.inverse().get(value);
    }

    /**
     * Checks if a specific ID is currently present in the registry.
     *
     * @param id The identifier to check.
     * @return {@code true} if an entry exists for the given ID.
     */
    public boolean contains(String id) {
        return entries.containsKey(id);
    }

    /**
     * Returns an unmodifiable view of all entries currently in the registry.
     *
     * @return A {@link Map} containing all ID-to-Object mappings.
     */
    public Map<String, T> getAll() {
        return Collections.unmodifiableMap(entries);
    }

    /**
     * Removes an entry from the registry by its ID.
     * <p>
     * <b>Warning:</b> This method is intended for internal use only. Removing entries
     * from a registry at runtime can cause significant issues with existing data
     * references and serialization.
     *
     * @param id The ID of the entry to remove.
     * @return The object that was removed, or {@code null} if the ID was not found.
     */
    @ApiStatus.Internal
    public T remove(String id) {
        return entries.remove(id);
    }
}