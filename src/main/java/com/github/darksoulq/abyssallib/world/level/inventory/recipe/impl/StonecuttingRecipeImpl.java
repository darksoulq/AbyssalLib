package com.github.darksoulq.abyssallib.world.level.inventory.recipe.impl;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.Recipe;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

/**
 * Represents a stonecutting recipe in the modding framework. This class extends {@link Recipe} and provides
 * methods to convert the recipe to a Bukkit {@link StonecuttingRecipe}, as well as serialize and deserialize it.
 */
public class StonecuttingRecipeImpl extends Recipe {
    public final ItemStack input;
    public final ItemStack result;

    /**
     * Creates a new StonecuttingRecipe with the specified parameters.
     *
     * @param id     the unique identifier for the recipe
     * @param input  the input item used in the recipe
     * @param result the resulting item after stonecutting
     */
    public StonecuttingRecipeImpl(Identifier id, ItemStack input, ItemStack result) {
        super(id);
        this.input = input;
        this.result = result;
    }

    /**
     * Converts this StonecuttingRecipe to a Bukkit {@link StonecuttingRecipe}.
     *
     * @return the corresponding Bukkit {@link StonecuttingRecipe}
     */
    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new StonecuttingRecipe(key, result.clone(), new RecipeChoice.ExactChoice(input));
    }
}
