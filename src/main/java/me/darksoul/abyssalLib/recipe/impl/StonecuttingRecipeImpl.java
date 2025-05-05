package me.darksoul.abyssalLib.recipe.impl;

import me.darksoul.abyssalLib.recipe.Recipe;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

public class StonecuttingRecipeImpl extends Recipe {
    public final ItemStack input;
    public final ItemStack result;

    public StonecuttingRecipeImpl(ResourceLocation id, ItemStack input, ItemStack result) {
        super(id);
        this.input = input;
        this.result = result;
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new StonecuttingRecipe(key, result.clone(), new RecipeChoice.ExactChoice(input));
    }
}
