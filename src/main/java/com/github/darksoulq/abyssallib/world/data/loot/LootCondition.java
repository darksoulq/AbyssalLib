package com.github.darksoulq.abyssallib.world.data.loot;

/**
 * Represents a condition that must be met for a loot entry to be considered valid
 * during loot table generation.
 */
public interface LootCondition {
    /**
     * Tests whether this condition passes in the given loot context.
     *
     * @param context the context in which loot is being generated
     * @return true if the condition passes and loot can be granted, false otherwise
     */
    boolean test(LootContext context);
}
