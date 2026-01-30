package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * A definition for a specific type of {@link LootFunction}.
 * <p>
 * This interface provides the {@link Codec} required to handle the data structure
 * for a specific implementation of a loot function.
 *
 * @param <F> The implementation class of {@link LootFunction}.
 */
public interface LootFunctionType<F extends LootFunction> {
    /**
     * @return The {@link Codec} used to serialize and deserialize functions of type {@code F}.
     */
    Codec<F> codec();
}