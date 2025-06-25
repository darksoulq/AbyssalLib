package com.github.darksoulq.abyssallib.world.level.inventory.gui.slot;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class AnimatedSlot extends Slot {
    private final Supplier<ItemStack> frameSupplier;
    private final AbstractGui.Type section;

    public AnimatedSlot(int index, AbstractGui.Type section, Supplier<ItemStack> frameSupplier) {
        super(index);
        this.frameSupplier = frameSupplier;
        this.section = section;
    }

    @Override
    public ActionResult onClick(AbstractGui gui, Inventory clickedInventory, Player player, ClickType type, InventoryAction action, ItemStack currentItem, ItemStack cursorItem) {
        return ActionResult.CANCEL;
    }

    @Override
    public void onTick(AbstractGui gui, Player player) {
        if (gui.inventory(player, section).getItem(index) == frameSupplier.get()) return;
        gui.inventory(player, section).setItem(index, frameSupplier.get());
    }
}
