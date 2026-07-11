package com.github.darksoulq.abyssallib.world.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DummyContainer implements Container {
    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty(Player player) {
        return true;
    }

    @Override
    public ItemStack getItem(Player player, int slot) {
        return null;
    }

    @Override
    public ItemStack removeItem(Player player, int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(Player player, int slot) {
        return null;
    }

    @Override
    public void setItem(Player player, int slot, ItemStack stack) {
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(Player player, int slot, ItemStack stack) {
        return false;
    }

    @Override
    public void clearContent(Player player) {
    }

    @Override
    public void addListener(ContainerListener listener) {
    }

    @Override
    public void removeListener(ContainerListener listener) {
    }
}