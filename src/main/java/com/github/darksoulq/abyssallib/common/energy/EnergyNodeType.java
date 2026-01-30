package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * Defines a specific type of energy node, providing the logic for its serialization.
 *
 * @param <T> The implementation of {@link EnergyNode}.
 */
public interface EnergyNodeType<T extends EnergyNode> {
    /**
     * Returns the codec used to serialize and deserialize this specific node type.
     * @return The {@link Codec} instance.
     */
    Codec<T> codec();
}