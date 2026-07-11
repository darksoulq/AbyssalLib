package com.github.darksoulq.abyssallib.world.menu.slot;

import com.github.darksoulq.abyssallib.world.menu.Container;
import com.github.darksoulq.abyssallib.world.menu.Slot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class Filtered extends Slot {
    private final Predicate<ItemStack> filter;

    public Filtered(Container container, int containerIndex, Predicate<ItemStack> filter) {
        super(container, containerIndex);
        this.filter = filter;
    }

    @Override
    public boolean mayPlace(Player player, ItemStack stack) {
        return this.filter.test(stack) && super.mayPlace(player, stack);
    }
}