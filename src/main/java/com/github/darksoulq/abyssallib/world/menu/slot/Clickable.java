package com.github.darksoulq.abyssallib.world.menu.slot;

import com.github.darksoulq.abyssallib.world.menu.ClickContext;
import com.github.darksoulq.abyssallib.world.menu.Container;
import com.github.darksoulq.abyssallib.world.menu.Slot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Clickable extends Slot {
    private final Consumer<ClickContext> onClick;

    public Clickable(Container container, int containerIndex, Consumer<ClickContext> onClick) {
        super(container, containerIndex);
        this.onClick = onClick;
    }

    @Override
    public boolean mayPlace(Player player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player, ItemStack stack) {
        return false;
    }

    public void onClick(ClickContext context) {
        this.onClick.accept(context);
    }
}