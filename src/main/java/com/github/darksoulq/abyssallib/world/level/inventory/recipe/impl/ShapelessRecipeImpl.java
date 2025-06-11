package com.github.darksoulq.abyssallib.world.level.inventory.recipe.impl;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.Recipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shapeless recipe in the modding framework. This extends the {@link Recipe} class and implements
 * the necessary methods to convert to a Bukkit {@link ShapelessRecipe} and serialize itself.
 */
public class ShapelessRecipeImpl extends Recipe {
    public final ItemStack result;
    public final List<ItemStack> ingredients = new ArrayList<>();

    /**
     * Creates a new ShapelessRecipe with the specified parameters.
     *
     * @param id     the unique identifier for the recipe
     * @param result the resulting item from the recipe
     */
    public ShapelessRecipeImpl(Identifier id, ItemStack result) {
        super(id);
        this.result = result;
    }

    /**
     * Adds an ingredient to the shapeless recipe.
     * A maximum of 6 ingredients can be added to a shapeless recipe.
     *
     * @param item the ingredient item to add
     * @return this ShapelessRecipeImpl instance
     */
    public ShapelessRecipeImpl addIngredient(ItemStack item) {
        if (ingredients.size() >= 6) {
            AbyssalLib.getInstance().getLogger().warning("Failed to add ingredient, only 6 ingredients can be added!");
            return this;
        }
        ingredients.add(item);
        return this;
    }

    /**
     * Converts this ShapelessRecipe to a Bukkit {@link ShapelessRecipe}.
     *
     * @return the corresponding Bukkit {@link ShapelessRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        ShapelessRecipe recipe = new ShapelessRecipe(id.toNamespace(), result.clone());
        ingredients.forEach(ingredient -> recipe.addIngredient(new RecipeChoice.ExactChoice(ingredient)));
        return recipe;
    }
}
