package com.github.darksoulq.abyssallib.world.data.loot.function;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunction;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunctionType;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Random;

public class ExplosionDecayFunction extends LootFunction {
    public static final Codec<ExplosionDecayFunction> CODEC = new Codec<>() {
        @Override
        public <D> ExplosionDecayFunction decode(DynamicOps<D> ops, D input) {
            return new ExplosionDecayFunction();
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, ExplosionDecayFunction value) {
            return ops.createMap(Collections.emptyMap());
        }
    };

    public static final LootFunctionType<ExplosionDecayFunction> TYPE = () -> CODEC;

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        Random random = context.random();
        float decay = 1.0f / random.nextFloat(); 
        int amount = stack.getAmount();
        int result = 0;
        for (int i = 0; i < amount; i++) {
            if (random.nextFloat() <= decay) {
                result++;
            }
        }
        stack.setAmount(result);
        return stack;
    }

    @Override
    public LootFunctionType<?> getType() {
        return TYPE;
    }
}