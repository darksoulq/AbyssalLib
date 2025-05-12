package com.github.darksoulq.abyssallib.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.event.context.gui.GuiCloseContext;
import com.github.darksoulq.abyssallib.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.gui.AbstractGui;
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
        gui.handleClick(new GuiClickContext(gui, event));
    }
    // UNFINISHED
    @SubscribeEvent
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        AbstractGui gui = AbyssalLib.GUI_MANAGER.openGuis.get(player);
        if (gui == null) return;
        gui.handleDrag(new GuiDragContext(gui, event));
    }
    // UNFINISHED END

    @SubscribeEvent
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        AbstractGui gui = AbyssalLib.GUI_MANAGER.openGuis.get(player);
        if (gui == null) return;
        AbyssalLib.GUI_MANAGER.openGuis.remove(player);
        gui.onClose(new GuiCloseContext(gui, event));
    }
}
