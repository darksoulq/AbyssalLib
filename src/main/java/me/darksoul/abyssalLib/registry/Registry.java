package me.darksoul.abyssalLib.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Registry<T> extends HashMap<String, Registry.RegistryEntry<T>> {

    public void register(String id, RegistryEntry<T> entry) {
        if (super.containsKey(id)) {
            throw new IllegalStateException("ID '" + id + "' already registered.");
        }
        super.put(id, entry);
    }

    public boolean contains(String id) {
        return super.containsKey(id);
    }

    public T get(String id) {
        RegistryEntry<T> entry = super.get(id);
        return entry != null ? entry.create(id) : null;
    }

    public Set<T> getAll() {
        Set<T> result = new HashSet<>(size());
        forEach((id, entry) -> result.add(entry.create(id)));
        return result;
    }

    public RegistryEntry<T> getEntry(String id) {
        return super.get(id);
    }

    @FunctionalInterface
    public interface RegistryEntry<T> {
        T create(String id);
    }
}
