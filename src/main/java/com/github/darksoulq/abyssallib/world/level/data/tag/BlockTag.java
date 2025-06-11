package com.github.darksoulq.abyssallib.world.level.data.tag;

import com.github.darksoulq.abyssallib.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public class BlockTag implements Tag<Block> {
    private final String id;
    private final Set<Block> entries = new HashSet<>();

    public BlockTag(String id) {
        this.id = id;
    }

    public BlockTag add(Block entry) {
        entries.add(entry);
        return this;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean contains(Block entry) {
        return entries.contains(entry);
    }

    @Override
    public Set<Block> values() {
        return Set.copyOf(entries);
    }
}
