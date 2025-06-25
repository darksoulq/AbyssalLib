package com.github.darksoulq.abyssallib.world.level.inventory.gui.slot;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ButtonSlot extends Slot {
    private final ItemStack display;
    private final GuiClickHandler clickHandler;

    public ButtonSlot(int index, ItemStack display, GuiClickHandler clickHandler) {
        super(index);
        this.display = display;
        this.clickHandler = clickHandler;
    }

    @Override
    public ActionResult onClick(AbstractGui gui, Inventory clickedInventory, Player player,
                                ClickType type, InventoryAction action, ItemStack currentItem, ItemStack cursor) {
        clickHandler.handle(gui, clickedInventory, player, type, action, currentItem, cursor);
        return ActionResult.CANCEL;
    }

    @Override
    public void onTick(AbstractGui gui, Player player) {

    }

    @FunctionalInterface
    public interface GuiClickHandler {
        void handle(AbstractGui gui, Inventory clickedInventory, Player player,
                    ClickType type, InventoryAction action,
                    ItemStack currentItem, ItemStack cursor);
    }
}
