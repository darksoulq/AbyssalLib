package com.github.darksoulq.abyssallib.world.level.data.loot;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a function that modifies a generated loot item.
 * Used to transform or adjust an ItemStack based on the loot context.
 */
public interface LootFunction {
    /**
     * Applies this function to the given item stack using the provided loot context.
     *
     * @param stack   the original ItemStack to modify
     * @param context the loot context providing relevant data for modification
     * @return the modified ItemStack
     */
    ItemStack apply(ItemStack stack, LootContext context);
}
