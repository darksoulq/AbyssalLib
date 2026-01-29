package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;

import java.util.Map;

public abstract class LootCondition {
    
    public abstract boolean test(LootContext context);
    public abstract LootConditionType<?> getType();

    public static final Codec<LootCondition> CODEC = new Codec<>() {
        @Override
        public <D> LootCondition decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            String typeId = ops.getStringValue(map.get(ops.createString("type"))).orElseThrow(() -> new CodecException("Missing condition type"));
            LootConditionType<?> type = Registries.LOOT_CONDITIONS.get(typeId);
            if (type == null) throw new CodecException("Unknown loot condition: " + typeId);
            return type.codec().decode(ops, input);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, LootCondition value) throws CodecException {
            LootConditionType<LootCondition> type = (LootConditionType<LootCondition>) value.getType();
            String id = Registries.LOOT_CONDITIONS.getId(type);
            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Codec must return map"));
            map.put(ops.createString("type"), ops.createString(id));
            return ops.createMap(map);
        }
    };
}