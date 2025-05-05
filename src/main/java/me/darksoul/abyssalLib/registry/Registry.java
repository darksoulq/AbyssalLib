package me.darksoul.abyssalLib.registry;

import me.darksoul.abyssalLib.item.Item;

import java.util.*;

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

    public List<T> getFor(String modID) {
        List<T> objects = new ArrayList<>();
        forEach((id, entry) -> {
            String modid = id.split(":")[0];
            if (modid.equals(modID)) objects.add(entry.create(id));
        });
        return objects;
    }

    public Map<String, T> getMap() {
        Map<String, T> map = new HashMap<>();
        forEach((id, entry) -> {
            map.put(id, entry.create(id));
        });
        return map;
    }

    public RegistryEntry<T> getEntry(String id) {
        return super.get(id);
    }

    @FunctionalInterface
    public interface RegistryEntry<T> {
        T create(String id);
    }
}
