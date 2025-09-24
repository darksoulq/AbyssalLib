package com.github.darksoulq.abyssallib.world.data.tag.impl;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.BlockBridge;
import com.github.darksoulq.abyssallib.server.bridge.block.BridgeBlock;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;

import java.util.HashSet;
import java.util.Set;

public class BlockTag extends Tag<BridgeBlock<?>> {
    public BlockTag(Identifier id) {
        super(id);
    }

    @Override
    public void add(BridgeBlock<?> value) {
        values.add(value.getId().toString());
    }

    @Override
    public boolean contains(BridgeBlock<?> value) {
        if (values.contains(value.getId().toString())) return true;
        for (Tag<BridgeBlock<?>> tag : included) {
            if (!tag.getValues().contains(value.getId().toString())) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<BridgeBlock<?>> getAll() {
        Set<BridgeBlock<?>> all = new HashSet<>(values.stream().map(BlockBridge::get).toList());
        included.forEach(i ->
                all.addAll(i.getValues().stream().map(BlockBridge::get).toList()));
        return all;
    }
}
