package com.github.darksoulq.abyssallib.recipe;

import com.github.darksoulq.abyssallib.gui.slot.Slot;
import com.github.darksoulq.abyssallib.util.ResourceLocation;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class CustomRecipe {
    protected final ResourceLocation id;

    public CustomRecipe(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    public abstract List<Slot> getSlots();

    public abstract boolean matches();

    public abstract ItemStack getResult();
}
