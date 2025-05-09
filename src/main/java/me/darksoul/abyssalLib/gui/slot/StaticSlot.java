package me.darksoul.abyssalLib.gui.slot;

import me.darksoul.abyssalLib.event.context.GuiClickContext;
import me.darksoul.abyssalLib.event.context.GuiDragContext;
import me.darksoul.abyssalLib.gui.AbstractGui;
import me.darksoul.abyssalLib.gui.Slot;
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

    }

    @Override
    public void onDrag(GuiDragContext ctx) {

    }

    @Override
    public void onTick(AbstractGui gui) {

    }
}
