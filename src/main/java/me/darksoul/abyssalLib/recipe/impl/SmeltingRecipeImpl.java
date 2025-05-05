package me.darksoul.abyssalLib.recipe.impl;

import me.darksoul.abyssalLib.recipe.Recipe;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class SmeltingRecipeImpl extends Recipe {
    public final ItemStack input;
    public final ItemStack result;
    public final float experience;
    public final int cookingTime;

    public SmeltingRecipeImpl(ResourceLocation id, ItemStack input, ItemStack result, float exp, int time) {
        super(id);
        this.input = input;
        this.result = result;
        this.experience = exp;
        this.cookingTime = time;
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new FurnaceRecipe(key, result.clone(), new RecipeChoice.ExactChoice(input), experience, cookingTime);
    }
}
