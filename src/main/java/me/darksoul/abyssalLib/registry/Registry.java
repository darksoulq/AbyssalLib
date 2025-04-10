package me.darksoul.abyssalLib.registry;

import java.util.*;

public class Registry<T> {
    private final Map<String, RegistryEntry<T>> entries = new HashMap<>();

    public void register(String id, RegistryEntry<T> entry) {
        if (entries.containsKey(id)) {
            throw new IllegalStateException("ID '" + id + "' already registered.");
        }
        entries.put(id, entry);
    }

    public boolean contains(String id) {
        return entries.containsKey(id);
    }

    public T get(String id) {
        RegistryEntry<T> entry = entries.get(id);
        return entry != null ? entry.create(id) : null;
    }

    public Set<T> getAll() {
        Set<T> toReturn = new HashSet<>();
        entries.forEach((id, entry) -> toReturn.add(entry.create(id)));
        return toReturn;
    }

    public RegistryEntry<T> getEntry(String id) {
        return entries.get(id);
    }

    @FunctionalInterface
    public interface RegistryEntry<T> {
        T create(String id);
    }
}
