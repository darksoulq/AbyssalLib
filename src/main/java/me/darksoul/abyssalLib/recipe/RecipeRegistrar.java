package me.darksoul.abyssalLib.recipe;

import me.darksoul.abyssalLib.registry.BuiltinRegistries;
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
