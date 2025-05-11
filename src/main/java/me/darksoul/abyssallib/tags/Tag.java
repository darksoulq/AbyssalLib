package me.darksoul.abyssallib.tags;

import me.darksoul.abyssallib.registry.Registry;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Tag<T> {
    private final String id;
    private final Set<String> entries = new LinkedHashSet<>();

    public Tag(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void add(String entryId) {
        entries.add(entryId);
    }

    public void addAll(Collection<String> entryIds) {
        entries.addAll(entryIds);
    }

    public Set<String> getEntryIds() {
        return Collections.unmodifiableSet(entries);
    }

    public Set<T> resolve(Registry<T> backingRegistry) {
        Set<T> resolved = new LinkedHashSet<>();
        for (String id : entries) {
            T entry = backingRegistry.get(id);
            if (entry != null) {
                resolved.add(entry);
            }
        }
        return resolved;
    }

    public boolean contains(String id) {
        return entries.contains(id);
    }
}
