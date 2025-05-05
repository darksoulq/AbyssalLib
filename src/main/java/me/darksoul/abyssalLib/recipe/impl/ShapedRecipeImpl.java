package me.darksoul.abyssalLib.recipe.impl;

import me.darksoul.abyssalLib.recipe.Recipe;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class ShapedRecipeImpl extends Recipe {
    public final ItemStack result;
    public String[] shape;
    public final Map<Character, ItemStack> ingredients = new HashMap<>();

    public ShapedRecipeImpl(ResourceLocation id, ItemStack result, String... shape) {
        super(id);
        this.result = result;
        this.shape = shape;
    }
    public ShapedRecipeImpl(ResourceLocation id, ItemStack result) {
        super(id);
        this.result = result;
    }

    public void shape(String[] shape) {
        this.shape = shape;
    }

    public ShapedRecipeImpl define(char key, ItemStack item) {
        ingredients.put(key, item);
        return this;
    }

    public int getWidth() {
        return shape.length > 0 ? shape[0].length() : 0;
    }

    public int getHeight() {
        return shape.length;
    }

    public ItemStack getIngredient(int index) {
        int width = getWidth();
        int x = index % width;
        int y = index / width;
        if (y >= shape.length || x >= shape[y].length()) return null;
        char key = shape[y].charAt(x);
        return ingredients.getOrDefault(key, null);
    }

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
