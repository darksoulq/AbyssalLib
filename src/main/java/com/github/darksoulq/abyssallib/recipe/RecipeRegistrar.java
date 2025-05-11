package com.github.darksoulq.abyssallib.recipe;

import com.github.darksoulq.abyssallib.registry.BuiltinRegistries;
import org.bukkit.Bukkit;

public class RecipeRegistrar {
    public static void registerAll() {
        for (Recipe recipe : BuiltinRegistries.RECIPES.getAll()) {
            org.bukkit.inventory.Recipe bukkitRecipe = recipe.toBukkit();
            if (bukkitRecipe != null) {
                Bukkit.addRecipe(bukkitRecipe);
            }
        }
    }
}
