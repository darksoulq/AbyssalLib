package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;

import java.util.HashMap;
import java.util.Map;

public class RandomChanceCondition extends LootCondition {
    public static final Codec<RandomChanceCondition> CODEC = new Codec<>() {
        @Override
        public <D> RandomChanceCondition decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            float chance = Codecs.FLOAT.decode(ops, map.get(ops.createString("chance")));
            return new RandomChanceCondition(chance);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, RandomChanceCondition value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("chance"), Codecs.FLOAT.encode(ops, value.chance));
            return ops.createMap(map);
        }
    };

    public static final LootConditionType<RandomChanceCondition> TYPE = () -> CODEC;

    private final float chance;

    public RandomChanceCondition(float chance) {
        this.chance = chance;
    }

    @Override
    public boolean test(LootContext context) {
        return context.random().nextFloat() < chance;
    }

    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}