package com.github.darksoulq.abyssallib.world.level.data.loot.impl;

import com.github.darksoulq.abyssallib.world.level.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.level.data.loot.LootFunction;
import org.bukkit.inventory.ItemStack;

/**
 * A loot function that sets the amount of an {@link ItemStack}
 * to a random value between a specified minimum and maximum (inclusive).
 * <p>
 * This is commonly used for stackable items where you want to vary the quantity
 * (e.g., dropping 1–5 iron ingots).
 */
public class SetCountFunction implements LootFunction {
    private final int min;
    private final int max;

    /**
     * Constructs a new SetCountFunction.
     *
     * @param min the minimum count (inclusive)
     * @param max the maximum count (inclusive)
     */
    public SetCountFunction(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Applies this function to the given item stack, setting its amount
     * to a random number between {@code min} and {@code max}.
     *
     * @param stack   the item stack to modify
     * @param context the loot context
     * @return the modified item stack with updated amount
     */
    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        int amount = context.getRandom().nextInt(max - min + 1) + min;
        stack.setAmount(amount);
        return stack;
    }
}
