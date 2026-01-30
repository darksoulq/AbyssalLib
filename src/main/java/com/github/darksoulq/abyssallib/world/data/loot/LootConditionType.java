package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * A definition for a specific implementation of a {@link LootCondition}.
 * <p>
 * This interface holds the {@link Codec} necessary for converting condition
 * data between its in-memory object form and its serialized representation.
 * </p>
 *
 * @param <C> The specific implementation class of {@link LootCondition}.
 */
public interface LootConditionType<C extends LootCondition> {

    /**
     * Retrieves the codec associated with this condition type.
     *
     * @return The {@link Codec} instance for conditions of type {@code C}.
     */
    Codec<C> codec();
}