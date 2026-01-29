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

public class SetCountFunction extends LootFunction {
    public static final Codec<SetCountFunction> CODEC = new Codec<>() {
        @Override
        public <D> SetCountFunction decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int min = Codecs.INT.decode(ops, map.get(ops.createString("min")));
            int max = Codecs.INT.decode(ops, map.get(ops.createString("max")));
            return new SetCountFunction(min, max);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, SetCountFunction value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("min"), Codecs.INT.encode(ops, value.min));
            map.put(ops.createString("max"), Codecs.INT.encode(ops, value.max));
            return ops.createMap(map);
        }
    };

    public static final LootFunctionType<SetCountFunction> TYPE = () -> CODEC;

    private final int min;
    private final int max;

    public SetCountFunction(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        stack.setAmount(context.random().nextInt(max - min + 1) + min);
        return stack;
    }

    @Override
    public LootFunctionType<?> getType() {
        return TYPE;
    }
}