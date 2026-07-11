package com.github.darksoulq.abyssallib.server.registry.modifier;

import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.RecipeLoader;

public class RecipeModifier implements DeferredRegistryModifier {

    @Override
    public void onRegister(String id, Object value) {
        if (value instanceof CustomRecipe recipe) {
            RecipeLoader.registerToBukkit(recipe);
        }
    }
}