package com.github.darksoulq.abyssallib.world.data.loot.function;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunction;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunctionType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class LootingEnchantFunction extends LootFunction {
    public static final Codec<LootingEnchantFunction> CODEC = new Codec<>() {
        @Override
        public <D> LootingEnchantFunction decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int limit = Codecs.INT.orElse(0).decode(ops, map.get(ops.createString("limit")));
            return new LootingEnchantFunction(limit);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, LootingEnchantFunction value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("limit"), Codecs.INT.encode(ops, value.limit));
            return ops.createMap(map);
        }
    };
    public static final LootFunctionType<LootingEnchantFunction> TYPE = () -> CODEC;

    private final int limit;

    public LootingEnchantFunction(int limit) {
        this.limit = limit;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        if (context.killer() == null) return stack;
        ItemStack tool = context.tool();
        if (tool != null) {
            int level = tool.getEnchantmentLevel(Enchantment.LOOTING);
            if (level > 0) {
                int bonus = context.random().nextInt(level + 1);
                stack.setAmount(Math.min(limit > 0 ? limit : 64, stack.getAmount() + bonus));
            }
        }
        return stack;
    }

    @Override
    public LootFunctionType<?> getType() {
        return TYPE;
    }
}