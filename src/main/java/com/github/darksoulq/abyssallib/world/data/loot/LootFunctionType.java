package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

public interface LootFunctionType<F extends LootFunction> {
    Codec<F> codec();
}