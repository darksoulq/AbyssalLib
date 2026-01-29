package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class RandomChanceWithLootingCondition extends LootCondition {
    public static final Codec<RandomChanceWithLootingCondition> CODEC = new Codec<>() {
        @Override
        public <D> RandomChanceWithLootingCondition decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            float chance = Codecs.FLOAT.decode(ops, map.get(ops.createString("chance")));
            float multiplier = Codecs.FLOAT.decode(ops, map.get(ops.createString("looting_multiplier")));
            return new RandomChanceWithLootingCondition(chance, multiplier);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, RandomChanceWithLootingCondition value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("chance"), Codecs.FLOAT.encode(ops, value.chance));
            map.put(ops.createString("looting_multiplier"), Codecs.FLOAT.encode(ops, value.multiplier));
            return ops.createMap(map);
        }
    };

    public static final LootConditionType<RandomChanceWithLootingCondition> TYPE = () -> CODEC;

    private final float chance;
    private final float multiplier;

    public RandomChanceWithLootingCondition(float chance, float multiplier) {
        this.chance = chance;
        this.multiplier = multiplier;
    }

    @Override
    public boolean test(LootContext context) {
        int level = 0;
        if (context.tool() != null) {
            level = context.tool().getEnchantmentLevel(Enchantment.LOOTING);
        }
        return context.random().nextFloat() < (chance + (level * multiplier));
    }

    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}