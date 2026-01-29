package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class LootFunction {

    public abstract ItemStack apply(ItemStack stack, LootContext context);
    public abstract LootFunctionType<?> getType();

    public static final Codec<LootFunction> CODEC = new Codec<>() {
        @Override
        public <D> LootFunction decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            String typeId = ops.getStringValue(map.get(ops.createString("type"))).orElseThrow(() -> new CodecException("Missing function type"));
            LootFunctionType<?> type = Registries.LOOT_FUNCTIONS.get(typeId);
            if (type == null) throw new CodecException("Unknown loot function: " + typeId);
            return type.codec().decode(ops, input);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, LootFunction value) throws CodecException {
            LootFunctionType<LootFunction> type = (LootFunctionType<LootFunction>) value.getType();
            String id = Registries.LOOT_FUNCTIONS.getId(type);
            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Codec must return map"));
            map.put(ops.createString("type"), ops.createString(id));
            return ops.createMap(map);
        }
    };
}