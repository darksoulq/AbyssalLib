package com.github.darksoulq.abyssallib.world.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VisualContainer implements Container {
    private final ItemStack[] items;

    public VisualContainer(int size) {
        this.items = new ItemStack[size];
    }

    @Override
    public int getContainerSize() {
        return this.items.length;
    }

    @Override
    public boolean isEmpty(Player player) {
        for (ItemStack item : this.items) {
            if (item != null && !item.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(Player player, int slot) {
        return slot >= 0 && slot < this.items.length ? this.items[slot] : null;
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
        if (slot >= 0 && slot < this.items.length) {
            this.items[slot] = stack;
        }
    }

    @Override
    public int getMaxStackSize() {
        return 64;
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