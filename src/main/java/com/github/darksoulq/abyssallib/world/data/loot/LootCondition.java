package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;

import java.util.Map;

/**
 * An abstract base class for logic gates that determine if a loot element should be processed.
 * <p>
 * LootConditions can be applied to both {@link LootPool}s and {@link LootEntry}s. If a
 * condition evaluates to {@code false}, the associated pool or entry is skipped entirely
 * for the current generation cycle.
 * </p>
 */
public abstract class LootCondition {

    /**
     * Evaluates this condition against the provided loot context.
     *
     * @param context The {@link LootContext} containing environment and entity data.
     * @return {@code true} if the condition is met; {@code false} otherwise.
     */
    public abstract boolean test(LootContext context);

    /**
     * Retrieves the specific type definition associated with this condition instance.
     *
     * @return The {@link LootConditionType} used for registry identification and serialization.
     */
    public abstract LootConditionType<?> getType();

    /**
     * Polymorphic codec for serializing and deserializing loot conditions.
     * <p>
     * Decodes the condition by identifying the "type" field via {@link Registries#LOOT_CONDITIONS}
     * and delegating to the specific type's internal codec.
     */
    public static final Codec<LootCondition> CODEC = new Codec<>() {
        /**
         * Decodes a loot condition from a serialized data structure.
         *
         * @param ops   The {@link DynamicOps} logic.
         * @param input The serialized data object.
         * @param <D>   The data type.
         * @return The decoded {@link LootCondition}.
         * @throws CodecException If the type field is missing or the ID is not registered.
         */
        @Override
        public <D> LootCondition decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            String typeId = ops.getStringValue(map.get(ops.createString("type"))).orElseThrow(() -> new CodecException("Missing condition type"));
            LootConditionType<?> type = Registries.LOOT_CONDITIONS.get(typeId);
            if (type == null) throw new CodecException("Unknown loot condition: " + typeId);
            return type.codec().decode(ops, input);
        }

        /**
         * Encodes a loot condition into a serialized data structure.
         *
         * @param ops   The {@link DynamicOps} logic.
         * @param value The {@link LootCondition} instance to encode.
         * @param <D>   The data type.
         * @return The encoded data object.
         * @throws CodecException If the condition type is not found in the registry.
         */
        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, LootCondition value) throws CodecException {
            LootConditionType<LootCondition> type = (LootConditionType<LootCondition>) value.getType();
            String id = Registries.LOOT_CONDITIONS.getId(type);
            if (id == null) throw new CodecException("Unregistered loot condition type");

            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Codec must return map"));
            map.put(ops.createString("type"), ops.createString(id));
            return ops.createMap(map);
        }
    };
}