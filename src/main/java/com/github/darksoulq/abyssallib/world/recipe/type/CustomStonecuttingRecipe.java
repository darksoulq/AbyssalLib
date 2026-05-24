package com.github.darksoulq.abyssallib.world.recipe.type;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.recipe.BukkitRecipeProvider;
import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class CustomStonecuttingRecipe implements CustomRecipe, BukkitRecipeProvider {
    public static final RecipeType<CustomStonecuttingRecipe> TYPE = () -> Codecs.STONECUTTING_RECIPE;

    private final NamespacedKey id;
    private final RecipeChoice input;
    private final ItemStack result;
    private final Optional<String> group;
    private final boolean replace;

    public CustomStonecuttingRecipe(NamespacedKey id, RecipeChoice input, ItemStack result, Optional<String> group, boolean replace) {
        this.id = id;
        this.input = input;
        this.result = result;
        this.group = group;
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

    public RecipeChoice getInput() {
        return input;
    }

    public ItemStack getResult() {
        return result;
    }

    public Optional<String> getGroup() {
        return group;
    }

    @Override
    public Recipe toBukkit() {
        StonecuttingRecipe recipe = new StonecuttingRecipe(id, result, input);
        group.ifPresent(recipe::setGroup);
        return recipe;
    }
}