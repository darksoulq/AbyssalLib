package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;

import java.util.HashMap;
import java.util.Map;

/**
 * A loot condition that evaluates to true based on a random probability check.
 * <p>
 * This is the standard condition used to create rare drops or optional loot
 * by defining a success rate between 0.0 and 1.0.
 * </p>
 */
public class RandomChanceCondition extends LootCondition {
    /**
     * The codec used for serializing and deserializing the random chance condition.
     * <p>
     * It requires a "chance" float field representing the probability of success.
     * </p>
     */
    public static final Codec<RandomChanceCondition> CODEC = new Codec<>() {
        /**
         * Decodes a RandomChanceCondition instance from the provided serialized data.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param input The serialized input data.
         * @param <D>   The type of the data being processed.
         * @return A new instance of {@link RandomChanceCondition}.
         * @throws CodecException If the "chance" field is missing or invalid.
         */
        @Override
        public <D> RandomChanceCondition decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            float chance = Codecs.FLOAT.decode(ops, map.get(ops.createString("chance")));
            return new RandomChanceCondition(chance);
        }

        /**
         * Encodes the RandomChanceCondition instance into a serialized format.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param value The condition instance to encode.
         * @param <D>   The type of the data being processed.
         * @return A map representing the encoded chance value.
         * @throws CodecException If the encoding process fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, RandomChanceCondition value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("chance"), Codecs.FLOAT.encode(ops, value.chance));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the random chance loot condition.
     */
    public static final LootConditionType<RandomChanceCondition> TYPE = () -> CODEC;

    /** The probability threshold for the condition to pass (0.0 to 1.0). */
    private final float chance;

    /**
     * Constructs a new RandomChanceCondition.
     *
     * @param chance The success probability, where 0.0 is never and 1.0 is always.
     */
    public RandomChanceCondition(float chance) {
        this.chance = chance;
    }

    /**
     * Performs a random check against the configured chance.
     * <p>
     * This uses the {@link java.util.Random} instance provided by the {@link LootContext}
     * to ensure consistent behavior with the rest of the loot generation cycle.
     * </p>
     *
     * @param context The {@link LootContext} providing the random source.
     * @return {@code true} if a random float [0, 1) is less than the chance; {@code false} otherwise.
     */
    @Override
    public boolean test(LootContext context) {
        return context.random().nextFloat() < chance;
    }

    /**
     * Retrieves the specific type definition for this loot condition.
     *
     * @return The {@link LootConditionType} associated with {@link RandomChanceCondition}.
     */
    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}