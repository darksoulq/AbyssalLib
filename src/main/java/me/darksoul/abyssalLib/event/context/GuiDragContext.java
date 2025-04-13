package me.darksoul.abyssalLib.event.context;

import me.darksoul.abyssalLib.gui.AbyssalGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class GuiDragContext {
    private final Player player;
    private final InventoryDragEvent event;
    private final AbyssalGui gui;

    public GuiDragContext(AbyssalGui gui, InventoryDragEvent event) {
        this.player = (Player) event.getWhoClicked();
        this.event = event;
        this.gui = gui;
    }

    public Player player() {
        return player;
    }

    public Set<Integer> draggedSlots() {
        return event.getRawSlots();
    }

    public ItemStack oldCursor() {
        return event.getOldCursor();
    }

    public void cancel() {
        event.setCancelled(true);
    }

    public AbyssalGui Gui() {
        return gui;
    }

    public InventoryDragEvent event() {
        return event;
    }
}
