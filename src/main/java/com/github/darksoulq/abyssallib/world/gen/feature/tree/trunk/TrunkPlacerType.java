package com.github.darksoulq.abyssallib.world.gen.feature.tree.trunk;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * Represents the registered type identifier for a specific trunk placer implementation.
 *
 * @param <P> The specific trunk placer class.
 */
public interface TrunkPlacerType<P extends TrunkPlacer> {

    /**
     * Retrieves the codec associated with this trunk placer type for serialization.
     *
     * @return The specific codec instance.
     */
    Codec<P> codec();
}