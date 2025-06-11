package com.github.darksoulq.abyssallib.world.level.inventory.recipe.impl;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.Recipe;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a brewing recipe in the modding framework. This extends the {@link Recipe} class and implements
 * methods for serialization and deserialization of brewing recipes.
 */
public class BrewingRecipeImpl extends Recipe {
    /**
     * The base input item (item in the three bottom slots).
     */
    public final ItemStack input;
    public final ItemStack ingredient;
    public final ItemStack result;

    /**
     * Constructs a new {@link BrewingRecipeImpl}.
     *
     * @param id         the unique identifier for the recipe
     * @param input      the base potion or input item
     * @param ingredient the ingredient used to modify the potion
     * @param result     the result of the brewing process
     */
    public BrewingRecipeImpl(Identifier id, ItemStack input, ItemStack ingredient, ItemStack result) {
        super(id);
        this.input = input;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        return null;
    }
}
