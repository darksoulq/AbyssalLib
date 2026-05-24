package com.github.darksoulq.abyssallib.world.recipe.type;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.recipe.BukkitRecipeProvider;
import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.TransmuteRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.Optional;

public class CustomTransmuteRecipe implements CustomRecipe, BukkitRecipeProvider {
    public static final RecipeType<CustomTransmuteRecipe> TYPE = () -> Codecs.TRANSMUTE_RECIPE;

    private final NamespacedKey id;
    private final RecipeChoice input;
    private final RecipeChoice material;
    private final ItemStack result;
    private final Optional<String> group;
    private final Optional<CraftingBookCategory> category;
    private final boolean replace;

    public CustomTransmuteRecipe(NamespacedKey id, RecipeChoice input, RecipeChoice material, ItemStack result, Optional<String> group, Optional<CraftingBookCategory> category, boolean replace) {
        this.id = id;
        this.input = input;
        this.material = material;
        this.result = result;
        this.group = group;
        this.category = category;
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

    public RecipeChoice getMaterial() {
        return material;
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
        TransmuteRecipe recipe = new TransmuteRecipe(id, result.getType(), input, material);
        group.ifPresent(recipe::setGroup);
        category.ifPresent(recipe::setCategory);
        return recipe;
    }
}