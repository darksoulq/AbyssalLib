package com.github.darksoulq.abyssallib.world.gen.feature.tree.foliage;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * Represents the registered type identifier for a specific foliage placer implementation.
 *
 * @param <P> The specific foliage placer class.
 */
public interface FoliagePlacerType<P extends FoliagePlacer> {

    /**
     * Retrieves the codec associated with this foliage placer type for serialization.
     *
     * @return The specific codec instance.
     */
    Codec<P> codec();
}