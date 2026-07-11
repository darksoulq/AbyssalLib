package com.github.darksoulq.abyssallib.world.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class Slot {
    public final Container container;
    private final int containerIndex;
    public int index;

    public Slot(Container container, int containerIndex) {
        this.container = container;
        this.containerIndex = containerIndex;
    }

    public void onTake(Player player, ItemStack stack) {
        this.setChanged();
    }

    public boolean mayPlace(Player player, ItemStack stack) {
        return true;
    }

    public boolean mayPickup(Player player, ItemStack stack) {
        return true;
    }

    public boolean dropsOnClose(Player player, ItemStack stack) {
        return false;
    }

    public ItemStack getItem(Player player) {
        return this.container.getItem(player, this.containerIndex);
    }

    public boolean hasItem(Player player) {
        ItemStack item = this.getItem(player);
        return item != null && !item.isEmpty();
    }

    public void setItem(Player player, ItemStack stack) {
        this.container.setItem(player, this.containerIndex, stack);
        this.setChanged();
    }

    public void setChanged() {
        this.container.setChanged();
    }

    public int getMaxStackSize() {
        return this.container.getMaxStackSize();
    }

    public int getMaxStackSize(ItemStack stack) {
        return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
    }

    public Optional<ItemStack> tryRemove(Player player, int amount, int maxAmount) {
        ItemStack current = this.getItem(player);
        if (current == null || current.isEmpty() || !this.mayPickup(player, current)) {
            return Optional.empty();
        }
        ItemStack item = this.container.removeItem(player, this.containerIndex, Math.min(amount, maxAmount));
        if (item == null || item.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(item);
    }

    public ItemStack removeItem(Player player, int amount) {
        return this.container.removeItem(player, this.containerIndex, amount);
    }

    public int getContainerIndex() {
        return this.containerIndex;
    }
}