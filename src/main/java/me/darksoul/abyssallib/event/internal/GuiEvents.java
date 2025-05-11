package me.darksoul.abyssallib.event.internal;

import me.darksoul.abyssallib.AbyssalLib;
import me.darksoul.abyssallib.event.SubscribeEvent;
import me.darksoul.abyssallib.event.context.gui.GuiClickContext;
import me.darksoul.abyssallib.event.context.gui.GuiCloseContext;
import me.darksoul.abyssallib.event.context.gui.GuiDragContext;
import me.darksoul.abyssallib.gui.AbstractGui;
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
        if (!(event.getPlayer() instanceof  Player player)) return;
        AbstractGui gui = AbyssalLib.GUI_MANAGER.openGuis.get(player);
        if (gui == null) return;
        gui.onClose(new GuiCloseContext(gui, event));
    }
}
