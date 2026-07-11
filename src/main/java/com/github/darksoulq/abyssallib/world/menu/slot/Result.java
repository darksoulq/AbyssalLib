package com.github.darksoulq.abyssallib.world.menu.slot;

import com.github.darksoulq.abyssallib.world.menu.Container;
import com.github.darksoulq.abyssallib.world.menu.Slot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Result extends Slot {
    public Result(Container container, int containerIndex) {
        super(container, containerIndex);
    }

    @Override
    public boolean mayPlace(Player player, ItemStack stack) {
        return false;
    }
}