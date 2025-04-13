package me.darksoul.abyssalLib.recipe;

import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class CampfireRecipeImpl extends Recipe {
    private final ItemStack input;
    private final ItemStack result;
    private final float experience;
    private final int cookingTime;

    public CampfireRecipeImpl(ResourceLocation id, ItemStack input, ItemStack result, float exp, int time) {
        super(id);
        this.input = input;
        this.result = result;
        this.experience = exp;
        this.cookingTime = time;
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new CampfireRecipe(key, result.clone(), new RecipeChoice.ExactChoice(input), experience, cookingTime);
    }
}
