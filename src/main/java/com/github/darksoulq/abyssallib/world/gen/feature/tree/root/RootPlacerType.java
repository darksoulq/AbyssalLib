package com.github.darksoulq.abyssallib.world.gen.feature.tree.root;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * Represents the registered type identifier for a specific root placer implementation.
 *
 * @param <P> The specific root placer class.
 */
public interface RootPlacerType<P extends RootPlacer> {

    /**
     * Retrieves the codec associated with this root placer type for serialization.
     *
     * @return The specific codec instance.
     */
    Codec<P> codec();
}