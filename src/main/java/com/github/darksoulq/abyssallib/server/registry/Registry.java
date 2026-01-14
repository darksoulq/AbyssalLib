package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.Map;

public final class Registry<T> {

    private final BiMap<String, T> entries = HashBiMap.create();

    public void register(String id, T object) {
        if (entries.containsKey(id)) {
            AbyssalLib.getInstance().getLogger().severe("ID '" + id + "' already registered! Skipping...");
            return;
        }
        entries.put(id, object);
    }

    public T get(String id) {
        return entries.get(id);
    }
    public String getId(T value) {
        return entries.inverse().get(value);
    }

    public boolean contains(String id) {
        return entries.containsKey(id);
    }
    public Map<String, T> getAll() {
        return Collections.unmodifiableMap(entries);
    }

    @ApiStatus.Internal
    public T remove(String id) {
        return entries.remove(id);
    }
}
