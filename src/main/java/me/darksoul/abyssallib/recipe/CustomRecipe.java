package me.darksoul.abyssallib.recipe;

import me.darksoul.abyssallib.gui.slot.Slot;
import me.darksoul.abyssallib.util.ResourceLocation;
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
