package com.github.darksoulq.abyssallib.world.level.data.loot;

import java.util.Random;

/**
 * Context object used during loot table evaluation.
 * Provides access to necessary information such as randomness.
 * Passed to loot conditions and functions to determine loot outcomes.
 */
public class LootContext {
    private final Random random = new Random();

    /**
     * Returns the random number generator used for loot calculations.
     *
     * @return the random instance for generating random values during loot evaluation
     */
    public Random getRandom() {
        return random;
    }
}
