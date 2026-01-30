package com.github.darksoulq.abyssallib.world.data.tag.impl;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.BridgeBlock;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a {@link Tag} for blocks, matching their ID strings.
 */
public class BlockTag extends Tag<String, BridgeBlock<?>> {
    /**
     * @param id The tag identifier.
     */
    public BlockTag(Identifier id) {
        super(id);
    }

    /**
     * Checks if the given BridgeBlock's ID is present in this tag or included tags.
     *
     * @param value The {@link BridgeBlock} to test.
     * @return {@code true} if the block ID matches.
     */
    @Override
    public boolean contains(BridgeBlock<?> value) {
        String blockId = value.id().toString();

        if (values.contains(blockId)) return true;

        for (Tag<String, BridgeBlock<?>> tag : included) {
            if (tag.contains(value)) return true;
        }
        return false;
    }

    /**
     * Flattens all block ID strings from this and included tags.
     *
     * @return A {@link Set} of all applicable block ID strings.
     */
    @Override
    public Set<String> getAll() {
        Set<String> all = new HashSet<>(values);
        for (Tag<String, BridgeBlock<?>> t : included) {
            all.addAll(t.getAll());
        }
        return all;
    }
}