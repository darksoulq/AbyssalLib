package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.data.tag.impl.BlockTag;
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;

import java.util.function.Function;

/**
 * A record defining a category of tags, providing its storage location and serialization logic.
 *
 * @param <T>     The entry type.
 * @param <D>     The test input type.
 * @param folder  The sub-folder name within the {@code tags/} directory.
 * @param codec   The {@link Codec} used to parse entries from YAML.
 * @param factory A {@link Function} to instantiate new tags of this type.
 */
public record TagType<T, D>(String folder, Codec<T> codec, Function<Identifier, Tag<T, D>> factory) {
    /** The default tag type for Items, using predicates and testing against ItemStacks. */
    public static final TagType<ItemPredicate, ?> ITEM = new TagType<>(
        "items", ItemPredicate.CODEC, ItemTag::new
    );

    /** The default tag type for Blocks, using ID strings and testing against BridgeBlocks. */
    public static final TagType<String, ?> BLOCK = new TagType<>(
        "blocks", Codecs.STRING, BlockTag::new
    );
}