package com.github.darksoulq.abyssallib.world.recipe;

import com.github.darksoulq.abyssallib.common.serialization.Codec;

public interface RecipeType<T extends CustomRecipe> {
    Codec<T> codec();
}