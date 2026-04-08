package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * Defines the registration type and serialization logic for a specific {@link AdvancementCriterion}.
 *
 * @param <T>
 * The specific implementation type of the advancement criterion.
 */
public interface CriterionType<T extends AdvancementCriterion> {

    /**
     * Retrieves the codec responsible for serializing and deserializing this criterion type.
     *
     * @return
     * The {@link Codec} instance for type {@code T}.
     */
    Codec<T> getCodec();
}