package me.darksoul.abyssalLib.gui;

import org.bukkit.inventory.ItemStack;

public abstract class Slot {
    protected final int index;

    public Slot(int index) {
        this.index = index;
    }

    public abstract ItemStack item();
    public abstract void item(ItemStack item);
    public abstract boolean canInsert(ItemStack stack);
    public abstract void onClick(GuiClickContext ctx);
    public abstract void onDrag(GuiDragContext ctx);
    public abstract void onTick(AbyssalGui gui);
}
