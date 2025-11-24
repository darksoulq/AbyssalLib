package com.github.darksoulq.abyssallib.world.recipe;

import com.github.darksoulq.abyssallib.common.util.Identifier;

public abstract class CustomRecipe<T extends CustomRecipe<T>> {
    private final Identifier id;

    public CustomRecipe(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }
}
