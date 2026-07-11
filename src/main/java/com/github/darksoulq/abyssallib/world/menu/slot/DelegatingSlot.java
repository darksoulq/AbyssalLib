package com.github.darksoulq.abyssallib.world.menu.slot;

import com.github.darksoulq.abyssallib.world.menu.Container;
import com.github.darksoulq.abyssallib.world.menu.Slot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DelegatingSlot extends Slot {
    private Slot delegate;

    public DelegatingSlot(Container container, int containerIndex) {
        super(container, containerIndex);
    }

    public void setDelegate(Slot delegate) {
        this.delegate = delegate;
        this.setChanged();
    }

    public Slot getDelegate() {
        return this.delegate;
    }

    @Override
    public ItemStack getItem(Player player) {
        return this.delegate != null ? this.delegate.getItem(player) : null;
    }

    @Override
    public boolean mayPlace(Player player, ItemStack stack) {
        return this.delegate != null && this.delegate.mayPlace(player, stack);
    }

    @Override
    public boolean mayPickup(Player player, ItemStack stack) {
        return this.delegate != null && this.delegate.mayPickup(player, stack);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        if (this.delegate != null) {
            this.delegate.onTake(player, stack);
        } else {
            super.onTake(player, stack);
        }
    }

    @Override
    public boolean dropsOnClose(Player player, ItemStack stack) {
        return this.delegate != null && this.delegate.dropsOnClose(player, stack);
    }
}