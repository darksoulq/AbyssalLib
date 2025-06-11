package com.github.darksoulq.abyssallib.world.level.inventory.recipe.impl;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.Recipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shaped recipe in the modding framework. This extends the {@link Recipe} class and implements
 * the necessary methods to convert to a Bukkit {@link ShapedRecipe} and serialize itself.
 */
public class ShapedRecipeImpl extends Recipe {
    public final ItemStack result;
    public String[] shape;
    public final Map<Character, ItemStack> ingredients = new HashMap<>();

    /**
     * Creates a new ShapedRecipe with the specified parameters.
     *
     * @param id     the unique identifier for the recipe
     * @param result the resulting item from the recipe
     * @param shape  the shape of the crafting grid
     */
    public ShapedRecipeImpl(Identifier id, ItemStack result, String... shape) {
        super(id);
        this.result = result;
        this.shape = shape;
    }
    /**
     * Creates a new ShapedRecipe with the specified parameters (without a predefined shape).
     *
     * @param id     the unique identifier for the recipe
     * @param result the resulting item from the recipe
     */
    public ShapedRecipeImpl(Identifier id, ItemStack result) {
        super(id);
        this.result = result;
    }

    /**
     * Sets the shape for the recipe.
     *
     * @param shape the shape of the crafting grid
     */
    public void shape(String[] shape) {
        this.shape = shape;
    }

    /**
     * Defines an ingredient for the recipe, mapped by its key character.
     *
     * @param key  the character key for the ingredient
     * @param item the ingredient item
     * @return this ShapedRecipeImpl instance
     */
    public ShapedRecipeImpl define(char key, ItemStack item) {
        ingredients.put(key, item);
        return this;
    }

    /**
     * Gets the width of the shaped recipe based on the shape.
     *
     * @return the width of the recipe shape
     */
    public int getWidth() {
        return shape.length > 0 ? shape[0].length() : 0;
    }
    /**
     * Gets the height of the shaped recipe based on the shape.
     *
     * @return the height of the recipe shape
     */
    public int getHeight() {
        return shape.length;
    }

    /**
     * Gets the ingredient at the specified index in the shape.
     *
     * @param index the index of the ingredient
     * @return the ingredient at the specified index, or null if not present
     */
    public ItemStack getIngredient(int index) {
        int width = getWidth();
        int x = index % width;
        int y = index / width;
        if (y >= shape.length || x >= shape[y].length()) return null;
        char key = shape[y].charAt(x);
        return ingredients.getOrDefault(key, null);
    }

    /**
     * Converts this ShapedRecipe to a Bukkit {@link ShapedRecipe}.
     *
     * @return the corresponding Bukkit {@link ShapedRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        ShapedRecipe recipe = new ShapedRecipe(id.toNamespace(), result.clone());
        recipe.shape(shape);
        for (var entry : ingredients.entrySet()) {
            recipe.setIngredient(entry.getKey(), new RecipeChoice.ExactChoice(entry.getValue()));
        }
        return recipe;
    }
}
