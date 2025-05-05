package me.darksoul.abyssalLib.recipe.impl;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.recipe.Recipe;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipeImpl extends Recipe {
    public final ItemStack result;
    public final List<ItemStack> ingredients = new ArrayList<>();

    public ShapelessRecipeImpl(ResourceLocation id, ItemStack result) {
        super(id);
        this.result = result;
    }

    public ShapelessRecipeImpl addIngredient(ItemStack item) {
        if (ingredients.size() >= 6) {
            AbyssalLib.getInstance().getLogger().warning("Failed to add ingredient, only 6 ingredients can be added!");
            return this;
        }
        ingredients.add(item);
        return this;
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        ShapelessRecipe recipe = new ShapelessRecipe(id.toNamespace(), result.clone());
        ingredients.forEach(ingredient -> {
            recipe.addIngredient(new RecipeChoice.ExactChoice(ingredient));
        });
        return recipe;
    }
}
