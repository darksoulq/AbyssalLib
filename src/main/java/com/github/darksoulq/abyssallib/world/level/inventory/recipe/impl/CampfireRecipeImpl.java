package com.github.darksoulq.abyssallib.world.level.inventory.recipe.impl;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.Recipe;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

/**
 * Represents a campfire recipe in the modding framework. This extends the {@link Recipe} class and implements
 * the necessary methods to convert to a Bukkit {@link CampfireRecipe} and serialize itself.
 */
public class CampfireRecipeImpl extends Recipe {
    public final ItemStack input;
    public final ItemStack result;
    public final float experience;
    public final int cookingTime;

    /**
     * Creates a new CampfireRecipe with the specified parameters.
     *
     * @param id         the unique identifier for the recipe
     * @param input      the input item for the recipe
     * @param result     the resulting item from the recipe
     * @param exp        the experience gained from the recipe
     * @param time       the cooking time (in ticks)
     */
    public CampfireRecipeImpl(Identifier id, ItemStack input, ItemStack result, float exp, int time) {
        super(id);
        this.input = input;
        this.result = result;
        this.experience = exp;
        this.cookingTime = time;
    }

    /**
     * Converts this CampfireRecipe to a Bukkit {@link CampfireRecipe}.
     *
     * @return the corresponding Bukkit {@link CampfireRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new CampfireRecipe(key, result.clone(), new RecipeChoice.ExactChoice(input), experience, cookingTime);
    }
}
