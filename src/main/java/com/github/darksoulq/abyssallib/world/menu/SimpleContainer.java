package com.github.darksoulq.abyssallib.world.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApiStatus.Experimental
public class SimpleContainer implements Container {
    private final ItemStack[] items;
    private final List<ContainerListener> listeners = new ArrayList<>();

    public SimpleContainer(int size) {
        this.items = new ItemStack[size];
    }

    public SimpleContainer(ItemStack... items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return this.items.length;
    }

    @Override
    public boolean isEmpty(Player player) {
        for (ItemStack item : this.items) {
            if (item != null && !item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(Player player, int slot) {
        return slot >= 0 && slot < this.items.length ? this.items[slot] : null;
    }

    @Override
    public ItemStack removeItem(Player player, int slot, int amount) {
        ItemStack item = this.getItem(player, slot);
        if (item == null || item.isEmpty()) return null;

        ItemStack result;
        if (item.getAmount() <= amount) {
            result = item;
            this.items[slot] = null;
        } else {
            result = item.clone();
            result.setAmount(amount);
            item.setAmount(item.getAmount() - amount);
        }
        this.setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(Player player, int slot) {
        ItemStack item = this.getItem(player, slot);
        if (item != null && !item.isEmpty()) {
            this.items[slot] = null;
            return item;
        }
        return null;
    }

    @Override
    public void setItem(Player player, int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.items.length) {
            this.items[slot] = stack;
            if (stack != null && stack.getAmount() > this.getMaxStackSize()) {
                stack.setAmount(this.getMaxStackSize());
            }
            this.setChanged();
        }
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
        for (ContainerListener listener : this.listeners) {
            listener.containerChanged(this);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(Player player, int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void clearContent(Player player) {
        Arrays.fill(this.items, null);
        this.setChanged();
    }

    @Override
    public void addListener(ContainerListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeListener(ContainerListener listener) {
        this.listeners.remove(listener);
    }
}