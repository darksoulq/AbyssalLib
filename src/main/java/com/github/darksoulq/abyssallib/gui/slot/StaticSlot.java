package com.github.darksoulq.abyssallib.gui.slot;

import com.github.darksoulq.abyssallib.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.gui.AbstractGui;
import org.bukkit.inventory.ItemStack;

public class StaticSlot extends Slot {
    private final ItemStack item;

    public StaticSlot(int index, ItemStack item) {
        super(index);
        this.item = item;
    }

    @Override
    public ItemStack item() {
        return item;
    }

    @Override
    public void item(ItemStack item) {

    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onClick(GuiClickContext ctx) {
        ctx.cancel();
    }

    @Override
    public void onTick(AbstractGui gui) {

    }
}
