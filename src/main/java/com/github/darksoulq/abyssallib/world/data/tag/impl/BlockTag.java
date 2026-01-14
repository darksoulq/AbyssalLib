package com.github.darksoulq.abyssallib.world.data.tag.impl;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.BridgeBlock;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;

import java.util.HashSet;
import java.util.Set;

public class BlockTag extends Tag<String, BridgeBlock<?>> {
    public BlockTag(Identifier id) {
        super(id);
    }

    @Override
    public boolean contains(BridgeBlock<?> value) {
        String blockId = value.id().toString();

        if (values.contains(blockId)) return true;

        for (Tag<String, BridgeBlock<?>> tag : included) {
            if (tag.contains(value)) return true;
        }
        return false;
    }

    @Override
    public Set<String> getAll() {
        Set<String> all = new HashSet<>(values);
        for (Tag<String, BridgeBlock<?>> t : included) {
            all.addAll(t.getAll());
        }
        return all;
    }
}