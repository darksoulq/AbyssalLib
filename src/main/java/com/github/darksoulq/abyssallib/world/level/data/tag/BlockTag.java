package com.github.darksoulq.abyssallib.world.level.data.tag;

import com.github.darksoulq.abyssallib.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BlockTag implements Tag<Block> {
    private final String id;
    private final Map<String, Block> entries = new HashMap<>();

    public BlockTag(String id) {
        this.id = id;
    }

    public BlockTag add(Block entry) {
        entries.put(entry.getId().toString(), entry);
        return this;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean contains(Block entry) {
        return entries.containsKey(entry.getId().toString());
    }

    @Override
    public Set<Block> values() {
        return Set.copyOf(entries.values());
    }
}
