package com.github.darksoulq.abyssallib.world.level.inventory.recipe;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;

/**
 * Represents a generic recipe in the modding framework. Specific recipe types should extend this class and
 * implement the methods to convert to Bukkit recipes and serialize themselves.
 */
public abstract class Recipe {
    public Identifier id;

    /**
     * Creates a new recipe with the specified identifier.
     *
     * @param id the unique identifier for the recipe
     */
    public Recipe(Identifier id) {
        this.id = id;
    }

    /**
     * Converts this recipe to a Bukkit recipe.
     *
     * @return the corresponding Bukkit recipe
     */
    public abstract org.bukkit.inventory.Recipe toBukkit();
}
