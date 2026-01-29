package com.github.darksoulq.abyssallib.world.data.loot.function;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunction;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunctionType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SetDamageFunction extends LootFunction {
    public static final Codec<SetDamageFunction> CODEC = new Codec<>() {
        @Override
        public <D> SetDamageFunction decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            float min = Codecs.FLOAT.decode(ops, map.get(ops.createString("min")));
            float max = Codecs.FLOAT.decode(ops, map.get(ops.createString("max")));
            return new SetDamageFunction(min, max);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, SetDamageFunction value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("min"), Codecs.FLOAT.encode(ops, value.min));
            map.put(ops.createString("max"), Codecs.FLOAT.encode(ops, value.max));
            return ops.createMap(map);
        }
    };

    public static final LootFunctionType<SetDamageFunction> TYPE = () -> CODEC;

    private final float min;
    private final float max;

    public SetDamageFunction(float min, float max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        float damagePercent = min + (max - min) * context.random().nextFloat();
        int maxDurability = stack.getType().getMaxDurability();
        Integer maxDamage = stack.getData(DataComponentTypes.MAX_DAMAGE);
        if (maxDamage != null) maxDurability = maxDamage;
        stack.setData(DataComponentTypes.DAMAGE, (int)(maxDurability * damagePercent));
        return stack;
    }

    @Override
    public LootFunctionType<?> getType() {
        return TYPE;
    }
}