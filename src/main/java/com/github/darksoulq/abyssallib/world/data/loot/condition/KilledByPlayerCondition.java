package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import org.bukkit.entity.Player;

import java.util.Collections;

public class KilledByPlayerCondition extends LootCondition {
    public static final Codec<KilledByPlayerCondition> CODEC = new Codec<>() {
        @Override
        public <D> KilledByPlayerCondition decode(DynamicOps<D> ops, D input) {
            return new KilledByPlayerCondition();
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, KilledByPlayerCondition value) {
            return ops.createMap(Collections.emptyMap());
        }
    };

    public static final LootConditionType<KilledByPlayerCondition> TYPE = () -> CODEC;

    @Override
    public boolean test(LootContext context) {
        return context.killer() instanceof Player;
    }

    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}