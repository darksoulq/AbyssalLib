package com.github.darksoulq.abyssallib.world.level.inventory.gui.slot;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class StaticSlot extends Slot {
    private final ItemStack item;
    private final AbstractGui.Type section;

    public StaticSlot(int index, AbstractGui.Type section, ItemStack item) {
        super(index);
        this.item = item;
        this.section = section;
    }

    @Override
    public ActionResult onClick(AbstractGui gui, Inventory clickedInventory, Player player, ClickType type, InventoryAction action, ItemStack currentItem, ItemStack cursorItem) {
        return ActionResult.CANCEL;
    }

    @Override
    public ActionResult onDrag(AbstractGui gui, @NotNull Inventory primaryInventory, @NotNull Set<Integer> slots, @NotNull Map<Integer, ItemStack> newItems, @Nullable ItemStack cursor, @NotNull ItemStack oldCursor, @NotNull DragType type) {
        return ActionResult.CANCEL;
    }

    @Override
    public void onTick(AbstractGui gui, Player player) {
        if (gui.inventory(player, section).getItem(index) == item) return;
        gui.inventory(player, section).setItem(index, item);
    }
}
