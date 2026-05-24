package com.github.darksoulq.abyssallib.world.recipe;

import net.kyori.adventure.key.Key;

public interface CustomRecipe {
    Key getKey();
    default boolean replace() {
        return false;
    }
    RecipeType<?> getType();
}