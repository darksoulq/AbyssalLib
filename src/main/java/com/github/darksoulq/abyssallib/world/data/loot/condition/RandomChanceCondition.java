package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;

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
    public static final Codec<RandomChanceCondition> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.fieldOf("chance").forGetter(RandomChanceCondition.class, p -> p.chance)
    ).apply(instance, RandomChanceCondition::new)).describe("RandomChanceCondition");

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