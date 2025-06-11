package com.github.darksoulq.abyssallib.world.level.inventory.recipe;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.slot.Slot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class CustomRecipe {
    protected final Identifier id;

    public CustomRecipe(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    public abstract List<Slot> getSlots();

    public abstract boolean matches();

    public abstract ItemStack getResult();
}
