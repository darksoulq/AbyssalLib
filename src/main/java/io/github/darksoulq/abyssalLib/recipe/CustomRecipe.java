package io.github.darksoulq.abyssalLib.recipe;

import io.github.darksoulq.abyssalLib.gui.slot.Slot;
import io.github.darksoulq.abyssalLib.util.ResourceLocation;
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
