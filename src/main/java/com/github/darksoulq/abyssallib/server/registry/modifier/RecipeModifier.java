package com.github.darksoulq.abyssallib.server.registry.modifier;

import com.github.darksoulq.abyssallib.world.recipe.BukkitRecipeProvider;
import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
//? <=26.2
//import com.github.darksoulq.abyssallib.world.recipe.PotionMixProvider;
import com.github.darksoulq.abyssallib.world.recipe.RecipeLoader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

public class RecipeModifier implements DeferredRegistryModifier {

    @Override
    public void onRegister(String id, Object value) {
        if (value instanceof CustomRecipe recipe) {
            RecipeLoader.registerToBukkit(recipe);
        }
    }

    @Override
    public void onUnload(String id, Object value) {
        if (value instanceof CustomRecipe recipe) {
            //? if <=26.2 {
            /*switch (recipe) {
                case BukkitRecipeProvider provider -> Bukkit.removeRecipe((NamespacedKey) recipe.getKey(), true);
                case PotionMixProvider provider -> Bukkit.getPotionBrewer().removePotionMix((NamespacedKey) recipe.getKey());
                default -> {
                }
            }
            *///?} else {
            if (recipe instanceof BukkitRecipeProvider provider) Bukkit.removeRecipe((NamespacedKey) recipe.getKey(), true);
            //?}
        }
    }
}