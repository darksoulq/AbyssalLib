package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.AbstractGui;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GuiEvents {
    @SubscribeEvent
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        AbstractGui gui = AbyssalLib.GUI_MANAGER.openGuis.get(player);
        if (gui == null) return;
        if (gui.shouldHandle(AbstractGui.Type.TOP)
                && gui.inventory(player, AbstractGui.Type.TOP) == event.getClickedInventory()) {
            for (Slot slot : gui.getSlotList(player, AbstractGui.Type.TOP)) {
                if (slot.index() != event.getSlot()) continue;
                ActionResult result = slot.onClick(
                        gui,
                        event.getClickedInventory(),
                        player,
                        event.getClick(),
                        event.getAction(),
                        event.getCurrentItem(),
                        event.getCursor()
                );
                if (result == ActionResult.CANCEL) event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
        } else if (gui.shouldHandle(AbstractGui.Type.BOTTOM)
                && gui.inventory(player, AbstractGui.Type.BOTTOM) == event.getClickedInventory()) {
            for (Slot slot : gui.getSlotList(player, AbstractGui.Type.BOTTOM)) {
                if (slot.index() != event.getSlot()) continue;
                ActionResult result = slot.onClick(
                        gui,
                        event.getClickedInventory(),
                        player,
                        event.getClick(),
                        event.getAction(),
                        event.getCurrentItem(),
                        event.getCursor()
                );
                if (result == ActionResult.CANCEL) event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        AbstractGui gui = AbyssalLib.GUI_MANAGER.openGuis.get(player);
        if (gui == null) return;
        if (gui.shouldHandle(AbstractGui.Type.TOP)
                && gui.inventory(player, AbstractGui.Type.TOP) == event.getInventory()) {
            for (Slot slot : gui.getSlotList(player, AbstractGui.Type.TOP)) {
                if (!event.getInventorySlots().contains(slot.index())) continue;
                ActionResult result = slot.onDrag(
                        gui,
                        event.getInventory(),
                        event.getInventorySlots(),
                        event.getNewItems(),
                        event.getCursor(),
                        event.getOldCursor(),
                        event.getType()
                );
                if (result == ActionResult.CANCEL) event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
        } else if (gui.shouldHandle(AbstractGui.Type.BOTTOM)
                && gui.inventory(player, AbstractGui.Type.BOTTOM) == event.getInventory()) {
            for (Slot slot : gui.getSlotList(player, AbstractGui.Type.BOTTOM)) {
                if (!event.getInventorySlots().contains(slot.index())) continue;
                ActionResult result = slot.onDrag(
                        gui,
                        event.getInventory(),
                        event.getInventorySlots(),
                        event.getNewItems(),
                        event.getCursor(),
                        event.getOldCursor(),
                        event.getType()
                );
                if (result == ActionResult.CANCEL) event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        AbstractGui gui = AbyssalLib.GUI_MANAGER.openGuis.get(player);
        if (gui == null) return;
        AbyssalLib.GUI_MANAGER.openGuis.remove(player);
        gui.viewers().remove(player);
        gui.onClose(
                event.getInventory(),
                player,
                event.getReason()
        );
    }
}
