package com.github.darksoulq.abyssallib.world.level.data.loot;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a pool of loot entries from which items are randomly selected and generated.
 * Each pool has a specified number of rolls indicating how many entries to generate per invocation.
 * Conditions can be added to control when this loot pool is applicable.
 */
public class LootPool {
    private final List<LootEntry> entries = new ArrayList<>();
    private final int rolls;
    private final List<LootCondition> conditions = new ArrayList<>();

    /**
     * Creates a new loot pool with the given number of rolls.
     *
     * @param rolls the number of times to roll for loot entries when generating loot
     */
    public LootPool(int rolls) {
        this.rolls = rolls;
    }

    /**
     * Adds a loot entry to this pool.
     *
     * @param entry the loot entry to add
     * @return this pool, for chaining
     */
    public LootPool addEntry(LootEntry entry) {
        this.entries.add(entry);
        return this;
    }

    /**
     * Adds a condition which must all pass for this loot pool to be used.
     *
     * @param condition the loot condition to add
     * @return this pool, for chaining
     */
    public LootPool when(LootCondition condition) {
        this.conditions.add(condition);
        return this;
    }

    /**
     * Generates a list of loot items from this pool based on the context.
     * Rolls the pool the specified number of times, selecting random entries each roll.
     * If any condition fails, an empty list is returned.
     *
     * @param context the loot context containing random and other data
     * @return the generated list of ItemStacks
     */
    public List<ItemStack> generate(LootContext context) {
        if (!conditions.stream().allMatch(cond -> cond.test(context))) return List.of();

        List<ItemStack> result = new ArrayList<>();
        Random random = context.getRandom();

        for (int i = 0; i < rolls; i++) {
            LootEntry entry = entries.get(random.nextInt(entries.size()));
            result.addAll(entry.generate(context));
        }

        return result;
    }
}

