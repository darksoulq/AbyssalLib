package com.github.darksoulq.abyssallib.world.recipe;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.7.0-mc1.21.10")
public abstract class CustomRecipe<T extends CustomRecipe<T>> {
    private final Identifier id;

    public CustomRecipe(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }
}
