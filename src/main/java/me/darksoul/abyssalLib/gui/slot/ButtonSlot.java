package me.darksoul.abyssalLib.gui.slot;

import me.darksoul.abyssalLib.gui.AbyssalGui;
import me.darksoul.abyssalLib.gui.GuiClickContext;
import me.darksoul.abyssalLib.gui.GuiDragContext;
import me.darksoul.abyssalLib.gui.Slot;
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
        clickHandler.accept(ctx);
    }

    @Override
    public void onDrag(GuiDragContext ctx) {}

    @Override
    public void onTick(AbyssalGui gui) {}
}
