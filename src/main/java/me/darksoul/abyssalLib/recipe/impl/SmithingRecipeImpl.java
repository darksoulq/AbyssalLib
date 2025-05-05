package me.darksoul.abyssalLib.recipe.impl;

import me.darksoul.abyssalLib.recipe.Recipe;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;

public class SmithingRecipeImpl extends Recipe {
    public final ItemStack template, base, addition, result;

    public SmithingRecipeImpl(ResourceLocation id, ItemStack template, ItemStack base, ItemStack addition, ItemStack result) {
        super(id);
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkit() {
        NamespacedKey key = id.toNamespace();
        return new SmithingTransformRecipe(
                key,
                result.clone(),
                new RecipeChoice.ExactChoice(template),
                new RecipeChoice.ExactChoice(base),
                new RecipeChoice.ExactChoice(addition),
                true
        );
    }
}
