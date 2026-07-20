package com.github.darksoulq.abyssallib.world.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Experimental
public class PlayerContainer implements Container {
    private final List<ContainerListener> listeners = new ArrayList<>();

    public PlayerContainer() {
    }

    @Override
    public int getContainerSize() {
        return 36;
    }

    @Override
    public boolean isEmpty(Player player) {
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && !item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(Player player, int slot) {
        return slot >= 0 && slot < 36 ? player.getInventory().getItem(slot) : null;
    }

    @Override
    public ItemStack removeItem(Player player, int slot, int amount) {
        ItemStack item = this.getItem(player, slot);
        if (item == null || item.isEmpty()) return null;

        ItemStack result;
        if (item.getAmount() <= amount) {
            result = item;
            player.getInventory().setItem(slot, null);
        } else {
            result = item.clone();
            result.setAmount(amount);
            item.setAmount(item.getAmount() - amount);
            player.getInventory().setItem(slot, item);
        }
        this.setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(Player player, int slot) {
        ItemStack item = this.getItem(player, slot);
        if (item != null && !item.isEmpty()) {
            player.getInventory().setItem(slot, null);
            return item;
        }
        return null;
    }

    @Override
    public void setItem(Player player, int slot, ItemStack stack) {
        if (slot >= 0 && slot < 36) {
            player.getInventory().setItem(slot, stack);
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
        for (int i = 0; i < 36; i++) {
            player.getInventory().setItem(i, null);
        }
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