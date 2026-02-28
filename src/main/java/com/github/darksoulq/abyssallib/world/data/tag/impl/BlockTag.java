package com.github.darksoulq.abyssallib.world.data.tag.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;
import com.github.darksoulq.abyssallib.world.data.tag.TagType;
import com.github.darksoulq.abyssallib.world.data.tag.TagTypes;
import net.kyori.adventure.key.Key;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a {@link Tag} for blocks, matching their ID strings against {@link BlockInfo}.
 */
public class BlockTag extends Tag<String, BlockInfo> {
    public static final TagType<String, BlockInfo> TYPE = new TagType<String, BlockInfo>() {
        @Override
        public Codec<String> codec() {
            return Codecs.STRING;
        }

        @Override
        public Tag<String, BlockInfo> create(Key id) {
            return new BlockTag(id);
        }
    };
    /**
     * Constructs a new BlockTag.
     *
     * @param id The tag identifier.
     */
    public BlockTag(Key id) {
        super(id);
    }

    /**
     * Retrieves the specific type of this tag.
     *
     * @return The {@link TagTypes#BLOCK} type.
     */
    @Override
    public TagType<String, BlockInfo> getType() {
        return TYPE;
    }

    /**
     * Checks if the given BlockInfo's ID is present in this tag or included tags.
     *
     * @param value The {@link BlockInfo} to test.
     * @return {@code true} if the block ID matches.
     */
    @Override
    public boolean contains(BlockInfo value) {
        String blockId = value.getAsString();

        if (values.contains(blockId)) return true;

        for (Tag<String, BlockInfo> tag : included) {
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
        for (Tag<String, BlockInfo> t : included) {
            all.addAll(t.getAll());
        }
        return all;
    }
}