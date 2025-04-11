package me.darksoul.abyssalLib.gui.slot;

import me.darksoul.abyssalLib.gui.AbyssalGui;
import me.darksoul.abyssalLib.gui.GuiClickContext;
import me.darksoul.abyssalLib.gui.GuiDragContext;
import me.darksoul.abyssalLib.gui.Slot;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class AnimatedSlot extends Slot {
    private final Supplier<ItemStack> frameSupplier;

    public AnimatedSlot(int index, Supplier<ItemStack> frameSupplier) {
        super(index);
        this.frameSupplier = frameSupplier;
    }

    @Override
    public ItemStack item() {
        return frameSupplier.get();
    }

    @Override
    public void item(ItemStack item) {

    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onClick(GuiClickContext ctx) {}
    @Override
    public void onDrag(GuiDragContext ctx) {}
    @Override
    public void onTick(AbyssalGui gui) {
        gui.inventory().setItem(index, item());
    }
}
