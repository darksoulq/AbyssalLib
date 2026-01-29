package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

public interface EnergyNodeType<T extends EnergyNode> {
    Codec<T> codec();
}