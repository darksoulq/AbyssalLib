package com.github.darksoulq.abyssallib.tag;

import com.github.darksoulq.abyssallib.item.Item;

import java.util.HashSet;
import java.util.Set;

public class ItemTag implements Tag<Item>{
    private final String id;
    private final Set<Item> entries = new HashSet<>();

    public ItemTag(String id) {
        this.id = id;
    }

    public ItemTag add(Item entry) {
        entries.add(entry);
        return this;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean contains(Item entry) {
        return entries.contains(entry);
    }

    @Override
    public Set<Item> values() {
        return Set.copyOf(entries);
    }
}
