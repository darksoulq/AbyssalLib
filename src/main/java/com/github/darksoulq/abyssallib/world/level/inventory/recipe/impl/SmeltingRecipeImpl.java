package com.github.darksoulq.abyssallib.world.level.inventory.recipe.impl;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.Recipe;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

/**
 * Represents a smelting recipe in the modding framework. This extends the {@link Recipe} class and implements
 * the necessary methods to convert to a Bukkit {@link FurnaceRecipe} and serialize itself.
 */
public class SmeltingRecipeImpl extends Recipe {
    public final ItemStack input;
    public final ItemStack result;
    public final float experience;
    public final int cookingTime;

    /**
     * Creates a new SmeltingRecipe with the specified parameters.
     *
     * @param id           the unique identifier for the recipe
     * @param input        the input item to be smelted
     * @param result       the resulting item after smelting
     * @param exp          the experience awarded
     * @param time         the cooking time in ticks
     */
    public SmeltingRecipeImpl(Identifier id, ItemStack input, ItemStack result, float exp, int time) {
        super(id);
        this.input = input;
        this.result = result;
        this.experience = exp;
        this.cookingTime = time;
    }

    /**
     * Converts this SmeltingRecipe to a Bukkit {@link FurnaceRecipe}.
     *
     * @return the corresponding Bukkit {@link FurnaceRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new FurnaceRecipe(key, result.clone(), new RecipeChoice.ExactChoice(input), experience, cookingTime);
    }
}
