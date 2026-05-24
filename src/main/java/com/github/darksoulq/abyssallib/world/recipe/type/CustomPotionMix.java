package com.github.darksoulq.abyssallib.world.recipe.type;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.PotionMixProvider;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ItemStack;

public class CustomPotionMix implements CustomRecipe, PotionMixProvider {
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

    public RecipeChoice getIngredient() {
        return ingredient;
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public PotionMix toPotionMix() {
        return new PotionMix(id, result, input, ingredient);
    }
}