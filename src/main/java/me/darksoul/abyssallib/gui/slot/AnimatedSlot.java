package me.darksoul.abyssallib.gui.slot;

import me.darksoul.abyssallib.event.context.gui.GuiClickContext;
import me.darksoul.abyssallib.gui.AbstractGui;
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
    public void onClick(GuiClickContext ctx) {
        ctx.cancel();
    }

    @Override
    public void onTick(AbstractGui gui) {

    }
}
