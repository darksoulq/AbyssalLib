package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;

import java.util.Collections;

public class SurvivesExplosionCondition extends LootCondition {
    public static final Codec<SurvivesExplosionCondition> CODEC = new Codec<>() {
        @Override
        public <D> SurvivesExplosionCondition decode(DynamicOps<D> ops, D input) {
            return new SurvivesExplosionCondition();
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, SurvivesExplosionCondition value) {
            return ops.createMap(Collections.emptyMap());
        }
    };

    public static final LootConditionType<SurvivesExplosionCondition> TYPE = () -> CODEC;

    @Override
    public boolean test(LootContext context) {
        return true; 
    }

    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}