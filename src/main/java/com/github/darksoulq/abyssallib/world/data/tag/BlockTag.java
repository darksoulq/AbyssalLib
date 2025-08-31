package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BlockTag implements Tag<CustomBlock> {
    private final String id;
    private final Map<String, CustomBlock> entries = new HashMap<>();

    public BlockTag(String id) {
        this.id = id;
    }

    public BlockTag add(CustomBlock entry) {
        entries.put(entry.getId().toString(), entry);
        return this;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean contains(CustomBlock entry) {
        return entries.containsKey(entry.getId().toString());
    }

    @Override
    public Set<CustomBlock> values() {
        return Set.copyOf(entries.values());
    }
}
