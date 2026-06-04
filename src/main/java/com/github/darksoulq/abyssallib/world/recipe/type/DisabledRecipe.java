package com.github.darksoulq.abyssallib.world.recipe.type;

import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NonNull;

public class DisabledRecipe implements CustomRecipe {

    private final NamespacedKey id;

    public DisabledRecipe(NamespacedKey id) {
        this.id = id;
    }

    @Override
    public @NonNull NamespacedKey getKey() {
        return id;
    }

    @Override
    public boolean replace() {
        return true;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
}