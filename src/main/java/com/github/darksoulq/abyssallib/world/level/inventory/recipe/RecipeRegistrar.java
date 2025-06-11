package com.github.darksoulq.abyssallib.world.level.inventory.recipe;

import com.github.darksoulq.abyssallib.server.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.impl.BrewingRecipeImpl;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.potion.PotionBrewer;

public class RecipeRegistrar {
    public static void registerAll() {
        PotionBrewer brewer = Bukkit.getPotionBrewer();
        for (Recipe recipe : BuiltinRegistries.RECIPES.getAll().values()) {
            if (recipe instanceof BrewingRecipeImpl) {
                brewer.addPotionMix(new PotionMix(
                        recipe.id.toNamespace(),
                        ((BrewingRecipeImpl) recipe).result,
                        new RecipeChoice.ExactChoice(((BrewingRecipeImpl) recipe).input),
                        new RecipeChoice.ExactChoice(((BrewingRecipeImpl) recipe).result))
                );
                continue;
            }
            org.bukkit.inventory.Recipe bukkitRecipe = recipe.toBukkit();
            if (bukkitRecipe != null) {
                Bukkit.addRecipe(bukkitRecipe);
            }
        }
    }
}
