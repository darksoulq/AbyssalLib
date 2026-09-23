package com.github.darksoulq.abyssallib.world.recipe.type;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;
import org.bukkit.NamespacedKey;
//? if <26.3 {
/*import io.papermc.paper.potion.PotionMix;
import com.github.darksoulq.abyssallib.world.recipe.PotionMixProvider;
*///?} else {
import org.bukkit.inventory.BrewingRecipe;
import com.github.darksoulq.abyssallib.world.recipe.BukkitRecipeProvider;
//?}
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jspecify.annotations.NonNull;

//? if <=26.2 {
/*public class CustomPotionMix implements CustomRecipe, PotionMixProvider {
*///?} else
public class CustomPotionMix implements CustomRecipe, BukkitRecipeProvider {
    public static final RecipeType<CustomPotionMix> TYPE = () -> Codecs.POTION_MIX;

    private final NamespacedKey id;
    private final RecipeChoice input;
    private final RecipeChoice ingredient;
    private final ItemStack result;
    private final boolean replace;

    public CustomPotionMix(NamespacedKey id, RecipeChoice input, RecipeChoice ingredient, ItemStack result, boolean replace) {
        this.id = id;
        this.input = input;
        this.ingredient = ingredient;
        this.result = result;
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

    public RecipeChoice getIngredient() {
        return ingredient;
    }

    public ItemStack getResult() {
        return result;
    }

    //? if <=26.2 {
    /*@Override
    public PotionMix toPotionMix() {
        return new PotionMix(id, result, input, ingredient);
    }
    *///?} else {
    @Override
    public BrewingRecipe toBukkit() {
        return new BrewingRecipe(id, result, input, ingredient);
    }
    //?}
}