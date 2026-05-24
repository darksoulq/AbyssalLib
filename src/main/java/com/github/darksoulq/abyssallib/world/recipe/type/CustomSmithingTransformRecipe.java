package com.github.darksoulq.abyssallib.world.recipe.type;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.recipe.BukkitRecipeProvider;
import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class CustomSmithingTransformRecipe implements CustomRecipe, BukkitRecipeProvider {
    public static final RecipeType<CustomSmithingTransformRecipe> TYPE = () -> Codecs.SMITHING_TRANSFORM_RECIPE;

    private final NamespacedKey id;
    private final RecipeChoice base;
    private final RecipeChoice template;
    private final RecipeChoice addition;
    private final ItemStack result;
    private final Optional<Boolean> copyComponents;
    private final boolean replace;

    public CustomSmithingTransformRecipe(NamespacedKey id, RecipeChoice base, RecipeChoice template, RecipeChoice addition, ItemStack result, Optional<Boolean> copyComponents, boolean replace) {
        this.id = id;
        this.base = base;
        this.template = template;
        this.addition = addition;
        this.result = result;
        this.copyComponents = copyComponents;
        this.replace = replace;
    }

    @Override
    public NamespacedKey getKey() {
        return id;
    }

    @Override
    public boolean replace() {
        return replace;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    public RecipeChoice getBase() {
        return base;
    }

    public RecipeChoice getTemplate() {
        return template;
    }

    public RecipeChoice getAddition() {
        return addition;
    }

    public ItemStack getResult() {
        return result;
    }

    public Optional<Boolean> getCopyComponents() {
        return copyComponents;
    }

    @Override
    public Recipe toBukkit() {
        if (copyComponents.isPresent()) {
            return new SmithingTransformRecipe(id, result, template, base, addition, copyComponents.get());
        }
        return new SmithingTransformRecipe(id, result, template, base, addition);
    }
}