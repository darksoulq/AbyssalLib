package com.github.darksoulq.abyssallib.world.gen.state.provider;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * Represents the registered type identifier for a specific block state provider implementation.
 *
 * @param <P> The specific block state provider class.
 */
public interface BlockStateProviderType<P extends BlockStateProvider> {

    /**
     * Retrieves the codec associated with this provider type for serialization.
     *
     * @return The specific codec instance.
     */
    Codec<P> codec();
}