package me.darksoul.abyssalLib.recipe;

import me.darksoul.abyssalLib.util.ResourceLocation;

public abstract class Recipe {
    protected final ResourceLocation id;

    public Recipe(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    public abstract org.bukkit.inventory.Recipe toBukkit();
}
