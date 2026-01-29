package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

public interface LootConditionType<C extends LootCondition> {
    Codec<C> codec();
}