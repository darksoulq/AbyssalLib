package com.github.darksoulq.abyssallib.world.data.loot.function;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunction;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunctionType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class LimitCountFunction extends LootFunction {
    public static final Codec<LimitCountFunction> CODEC = new Codec<>() {
        @Override
        public <D> LimitCountFunction decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int min = Codecs.INT.orElse(0).decode(ops, map.get(ops.createString("min")));
            int max = Codecs.INT.orElse(Integer.MAX_VALUE).decode(ops, map.get(ops.createString("max")));
            return new LimitCountFunction(min, max);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, LimitCountFunction value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("min"), Codecs.INT.encode(ops, value.min));
            map.put(ops.createString("max"), Codecs.INT.encode(ops, value.max));
            return ops.createMap(map);
        }
    };

    public static final LootFunctionType<LimitCountFunction> TYPE = () -> CODEC;

    private final int min;
    private final int max;

    public LimitCountFunction(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        int count = stack.getAmount();
        stack.setAmount(Math.max(min, Math.min(max, count)));
        return stack;
    }

    @Override
    public LootFunctionType<?> getType() {
        return TYPE;
    }
}