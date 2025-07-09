package com.github.darksoulq.abyssallib.world.level.data.tag;

import com.github.darksoulq.abyssallib.world.level.item.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemTag implements Tag<Item>{
    private final String id;
    private final Map<String, Item> entries = new HashMap<>();

    public ItemTag(String id) {
        this.id = id;
    }

    public ItemTag add(Item entry) {
        entries.put(entry.getId().toString(), entry);
        return this;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean contains(Item entry) {
        return entries.containsKey(entry.getId().toString());
    }

    @Override
    public Set<Item> values() {
        return Set.copyOf(entries.values());
    }
}
