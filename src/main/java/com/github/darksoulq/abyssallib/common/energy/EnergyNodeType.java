package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * Defines a serializable type of {@link EnergyNode}.
 *
 * @param <T> node implementation type
 */
public interface EnergyNodeType<T extends EnergyNode> {

    /**
     * Returns the codec used to serialize this node type.
     *
     * @return node codec
     */
    Codec<T> codec();
}