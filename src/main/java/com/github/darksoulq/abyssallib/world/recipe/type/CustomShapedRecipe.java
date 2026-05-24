package com.github.darksoulq.abyssallib.world.recipe.type;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.recipe.BukkitRecipeProvider;
import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomShapedRecipe implements CustomRecipe, BukkitRecipeProvider {
    public static final RecipeType<CustomShapedRecipe> TYPE = () -> Codecs.SHAPED_RECIPE;

    private final NamespacedKey id;
    private final List<String> shape;
    private final Map<Character, RecipeChoice> ingredients;
    private final ItemStack result;
    private final Optional<String> group;
    private final Optional<CraftingBookCategory> category;
    private final boolean replace;

    public CustomShapedRecipe(NamespacedKey id, List<String> shape, Map<Character, RecipeChoice> ingredients, ItemStack result, Optional<String> group, Optional<CraftingBookCategory> category, boolean replace) {
        this.id = id;
        this.shape = shape;
        this.ingredients = ingredients;
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

    public List<String> getShape() {
        return shape;
    }

    public Map<Character, RecipeChoice> getIngredients() {
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
        ShapedRecipe recipe = new ShapedRecipe(id, result);
        recipe.shape(shape.toArray(new String[0]));
        ingredients.forEach(recipe::setIngredient);
        group.ifPresent(recipe::setGroup);
        category.ifPresent(recipe::setCategory);
        return recipe;
    }
}