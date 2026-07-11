package com.github.darksoulq.abyssallib.world.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Container {
    int getContainerSize();

    boolean isEmpty(Player player);

    ItemStack getItem(Player player, int slot);

    ItemStack removeItem(Player player, int slot, int amount);

    ItemStack removeItemNoUpdate(Player player, int slot);

    void setItem(Player player, int slot, ItemStack stack);

    int getMaxStackSize();

    void setChanged();

    boolean stillValid(Player player);

    boolean canPlaceItem(Player player, int slot, ItemStack stack);

    void clearContent(Player player);

    void addListener(ContainerListener listener);

    void removeListener(ContainerListener listener);
}