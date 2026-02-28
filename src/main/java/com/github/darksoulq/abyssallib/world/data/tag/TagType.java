package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import net.kyori.adventure.key.Key;

/**
 * Represents a registry type for a {@link Tag}, defining its serialization codec
 * and the factory method used to create new tag instances.
 *
 * @param <T> The entry type of the tag.
 * @param <D> The test input type for the tag.
 */
public interface TagType<T, D> {

    /**
     * Retrieves the codec used for serializing and deserializing the tag entries.
     *
     * @return The {@link Codec} for the tag entry type.
     */
    Codec<T> codec();

    /**
     * Creates a new tag instance associated with this type.
     *
     * @param id The identifier for the new tag.
     * @return A newly instantiated {@link Tag}.
     */
    Tag<T, D> create(Key id);
}