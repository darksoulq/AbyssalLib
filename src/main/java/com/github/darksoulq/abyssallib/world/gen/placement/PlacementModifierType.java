package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

public interface PlacementModifierType<P extends PlacementModifier> {
    Codec<P> codec();
}