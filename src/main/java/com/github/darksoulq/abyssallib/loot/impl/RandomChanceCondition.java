package com.github.darksoulq.abyssallib.loot.impl;

import com.github.darksoulq.abyssallib.loot.LootCondition;
import com.github.darksoulq.abyssallib.loot.LootContext;

/**
 * A loot condition that passes based on a fixed random chance.
 * <p>
 * Useful for adding probabilistic behavior to loot pools, such as
 * "this item has a 25% chance to drop".
 */
public class RandomChanceCondition implements LootCondition {
    private final float chance;

    /**
     * Constructs a new RandomChanceCondition.
     *
     * @param chance the chance for the condition to pass, from 0.0 (never) to 1.0 (always)
     */
    public RandomChanceCondition(float chance) {
        this.chance = chance;
    }

    /**
     * Tests whether the loot should be generated based on the random chance.
     *
     * @param context the loot context
     * @return true if the condition passes, false otherwise
     */
    @Override
    public boolean test(LootContext context) {
        return context.getRandom().nextFloat() < chance;
    }
}
