package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

/**
 * A registry-friendly type definition for a {@link PlacementModifier}.
 *
 * @param <P> The specific implementation of PlacementModifier.
 */
public interface PlacementModifierType<P extends PlacementModifier> {
    /**
     * @return The codec used to handle data for this modifier type.
     */
    Codec<P> codec();
}