package com.github.darksoulq.abyssallib.loot;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents an entry in a loot table.
 * Defines how to generate a list of ItemStacks based on the provided loot context.
 */
public interface LootEntry {
    /**
     * Generates loot items according to the given context.
     *
     * @param context the loot context providing necessary data and randomness
     * @return a list of ItemStacks representing the generated loot
     */
    List<ItemStack> generate(LootContext context);
}
