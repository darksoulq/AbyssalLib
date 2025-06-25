package com.github.darksoulq.abyssallib.world.level.data.loot;

import java.util.Random;

/**
 * Context object used during loot table evaluation.
 * <p>
 * This provides the relevant data needed for loot generation,
 * such as the random number generator and the fortune enchantment level.
 * It is passed to loot conditions and functions to determine which items to drop.
 */
public class LootContext {

    /**
     * The random number generator used for loot calculations.
     * Ensures that drops are varied and follow probability rules.
     */
    private final Random random = new Random();

    /**
     * The level of the Fortune enchantment on the tool used to break the block.
     * This can be used by loot functions to increase the quantity or chances of specific drops.
     * A value of 0 means no Fortune effect.
     */
    private final int fortuneLevel;

    /**
     * Constructs a new LootContext with the given Fortune level.
     *
     * @param fortuneLevel the Fortune enchantment level of the tool (0 if not present)
     */
    public LootContext(int fortuneLevel) {
        this.fortuneLevel = fortuneLevel;
    }

    /**
     * Returns the random number generator used for loot calculations.
     *
     * @return the random instance for generating random values during loot evaluation
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Gets the level of the Fortune enchantment applied during loot calculation.
     *
     * @return the fortune level (0 if not present)
     */
    public int getFortuneLevel() {
        return fortuneLevel;
    }
}