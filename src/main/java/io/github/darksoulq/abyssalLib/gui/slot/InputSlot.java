package io.github.darksoulq.abyssalLib.gui.slot;

import io.github.darksoulq.abyssalLib.event.context.gui.GuiClickContext;
import io.github.darksoulq.abyssalLib.gui.AbstractGui;
import org.bukkit.inventory.ItemStack;

public class InputSlot extends Slot {
    private ItemStack item = null;
    public InputSlot(int index) {
        super(index);
    }

    @Override
    public ItemStack item() {
        return item == null ? null : item.clone();
    }

    @Override
    public void item(ItemStack item) {
        this.item = item == null ? null : item.clone();
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return true;
    }

    @Override
    public void onClick(GuiClickContext ctx) {
        ItemStack prev = item;
        item(ctx.event().getCursor());
        ctx.event().setCursor(prev);
    }

    @Override
    public void onTick(AbstractGui gui) {

    }
}
