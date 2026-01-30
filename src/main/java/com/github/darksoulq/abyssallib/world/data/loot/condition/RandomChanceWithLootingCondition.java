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

/**
 * A loot condition that evaluates to true based on a probability that increases with the Looting enchantment level.
 * <p>
 * This condition calculates success using the formula: {@code chance + (looting_level * multiplier)}.
 * It is primarily used for rare mob drops where the probability of the drop is intended to scale
 * with the player's equipment.
 * </p>
 */
public class RandomChanceWithLootingCondition extends LootCondition {

    /**
     * The codec used for serializing and deserializing the random chance with looting condition.
     * <p>
     * It maps the "chance" base probability and the "looting_multiplier" float which is
     * added per level of the Looting enchantment.
     * </p>
     */
    public static final Codec<RandomChanceWithLootingCondition> CODEC = new Codec<>() {
        /**
         * Decodes a RandomChanceWithLootingCondition instance from the provided serialized data.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param input The serialized input data.
         * @param <D>   The type of the data being processed.
         * @return A new instance of {@link RandomChanceWithLootingCondition}.
         * @throws CodecException If the "chance" or "looting_multiplier" fields are missing or invalid.
         */
        @Override
        public <D> RandomChanceWithLootingCondition decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            float chance = Codecs.FLOAT.decode(ops, map.get(ops.createString("chance")));
            float multiplier = Codecs.FLOAT.decode(ops, map.get(ops.createString("looting_multiplier")));
            return new RandomChanceWithLootingCondition(chance, multiplier);
        }

        /**
         * Encodes the RandomChanceWithLootingCondition instance into a serialized format.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param value The condition instance to encode.
         * @param <D>   The type of the data being processed.
         * @return A map representing the encoded chance and multiplier values.
         * @throws CodecException If the encoding process fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, RandomChanceWithLootingCondition value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("chance"), Codecs.FLOAT.encode(ops, value.chance));
            map.put(ops.createString("looting_multiplier"), Codecs.FLOAT.encode(ops, value.multiplier));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the random chance with looting loot condition.
     */
    public static final LootConditionType<RandomChanceWithLootingCondition> TYPE = () -> CODEC;

    /** The base probability threshold (0.0 to 1.0). */
    private final float chance;

    /** The amount added to the base chance per level of Looting. */
    private final float multiplier;

    /**
     * Constructs a new RandomChanceWithLootingCondition.
     *
     * @param chance     The base success probability.
     * @param multiplier The probability increase per level of the Looting enchantment.
     */
    public RandomChanceWithLootingCondition(float chance, float multiplier) {
        this.chance = chance;
        this.multiplier = multiplier;
    }

    /**
     * Performs a random check against the base chance plus the looting bonus.
     * <p>
     * The level of the {@link Enchantment#LOOTING} is retrieved from the tool present
     * in the {@link LootContext}. If no tool is present, the level defaults to 0.
     * </p>
     *
     * @param context The {@link LootContext} providing the tool and random source.
     * @return {@code true} if a random float [0, 1) is less than the calculated threshold; {@code false} otherwise.
     */
    @Override
    public boolean test(LootContext context) {
        int level = 0;
        if (context.tool() != null) {
            level = context.tool().getEnchantmentLevel(Enchantment.LOOTING);
        }
        return context.random().nextFloat() < (chance + (level * multiplier));
    }

    /**
     * Retrieves the specific type definition for this loot condition.
     *
     * @return The {@link LootConditionType} associated with {@link RandomChanceWithLootingCondition}.
     */
    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}