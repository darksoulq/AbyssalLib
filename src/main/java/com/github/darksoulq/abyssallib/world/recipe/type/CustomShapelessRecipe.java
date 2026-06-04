package com.github.darksoulq.abyssallib.world.recipe.type;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.recipe.BukkitRecipeProvider;
import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class CustomShapelessRecipe implements CustomRecipe, BukkitRecipeProvider {
    public static final RecipeType<CustomShapelessRecipe> TYPE = () -> Codecs.SHAPELESS_RECIPE;

    private final NamespacedKey id;
    private final List<RecipeChoice> ingredients;
    private final ItemStack result;
    private final Optional<String> group;
    private final Optional<CraftingBookCategory> category;
    private final boolean replace;

    public CustomShapelessRecipe(NamespacedKey id, List<RecipeChoice> ingredients, ItemStack result, Optional<String> group, Optional<CraftingBookCategory> category, boolean replace) {
        this.id = id;
        this.ingredients = ingredients;
        this.result = result;
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

    public List<RecipeChoice> getIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    public Optional<String> getGroup() {
        return group;
    }

    public Optional<CraftingBookCategory> getCategory() {
        return category;
    }

    @Override
    public Recipe toBukkit() {
        ShapelessRecipe recipe = new ShapelessRecipe(id, result);
        ingredients.forEach(recipe::addIngredient);
        group.ifPresent(recipe::setGroup);
        category.ifPresent(recipe::setCategory);
        return recipe;
    }
}