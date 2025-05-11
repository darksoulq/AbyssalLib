package com.github.darksoulq.abyssallib.gui.slot;

import com.github.darksoulq.abyssallib.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.gui.AbstractGui;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ButtonSlot extends Slot {
    private final ItemStack display;
    private final Consumer<GuiClickContext> clickHandler;

    public ButtonSlot(int index, ItemStack display, Consumer<GuiClickContext> clickHandler) {
        super(index);
        this.display = display;
        this.clickHandler = clickHandler;
    }

    @Override
    public ItemStack item() {
        return display;
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
        clickHandler.accept(ctx);
    }

    @Override
    public void onTick(AbstractGui gui) {

    }
}
