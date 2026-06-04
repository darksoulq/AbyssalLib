package com.github.darksoulq.abyssallib.world.recipe.type;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.recipe.BukkitRecipeProvider;
import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class CustomSmokingRecipe implements CustomRecipe, BukkitRecipeProvider {
    public static final RecipeType<CustomSmokingRecipe> TYPE = () -> Codecs.SMOKING_RECIPE;

    private final NamespacedKey id;
    private final RecipeChoice input;
    private final ItemStack result;
    private final int cookingTime;
    private final float exp;
    private final Optional<String> group;
    private final Optional<CookingBookCategory> category;
    private final boolean replace;

    public CustomSmokingRecipe(NamespacedKey id, RecipeChoice input, ItemStack result, int cookingTime, float exp, Optional<String> group, Optional<CookingBookCategory> category, boolean replace) {
        this.id = id;
        this.input = input;
        this.result = result;
        this.cookingTime = cookingTime;
        this.exp = exp;
        this.group = group;
        this.category = category;
        this.replace = replace;
    }

    @Override
    public @NonNull NamespacedKey getKey() {
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

    public int getCookingTime() {
        return cookingTime;
    }

    public float getExp() {
        return exp;
    }

    public Optional<String> getGroup() {
        return group;
    }

    public Optional<CookingBookCategory> getCategory() {
        return category;
    }

    @Override
    public Recipe toBukkit() {
        SmokingRecipe recipe = new SmokingRecipe(id, result, input, exp, cookingTime);
        group.ifPresent(recipe::setGroup);
        category.ifPresent(recipe::setCategory);
        return recipe;
    }
}