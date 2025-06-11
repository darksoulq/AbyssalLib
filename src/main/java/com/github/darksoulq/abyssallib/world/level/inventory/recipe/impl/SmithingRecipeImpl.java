package com.github.darksoulq.abyssallib.world.level.inventory.recipe.impl;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.Recipe;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;

/**
 * Represents a smithing recipe in the modding framework. This class extends {@link Recipe} and provides
 * methods to convert the recipe to a Bukkit {@link SmithingTransformRecipe}, as well as serialize and deserialize it.
 */
public class SmithingRecipeImpl extends Recipe {
    public final ItemStack template, base, addition, result;

    /**
     * Creates a new SmithingRecipe with the specified parameters.
     *
     * @param id        the unique identifier for the recipe
     * @param template  the template item used in the recipe
     * @param base      the base item used in the recipe
     * @param addition  the additional item used in the recipe
     * @param result    the resulting item after smithing
     */
    public SmithingRecipeImpl(Identifier id, ItemStack template, ItemStack base, ItemStack addition, ItemStack result) {
        super(id);
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    /**
     * Converts this SmithingRecipe to a Bukkit {@link SmithingTransformRecipe}.
     *
     * @return the corresponding Bukkit {@link SmithingTransformRecipe}
     */
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
