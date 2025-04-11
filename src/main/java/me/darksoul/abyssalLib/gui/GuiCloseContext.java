package me.darksoul.abyssalLib.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiCloseContext {
    private final Player player;
    private final InventoryCloseEvent event;
    private final AbyssalGui gui;

    public GuiCloseContext(AbyssalGui gui, InventoryCloseEvent event) {
        this.player = (Player) event.getPlayer();
        this.event = event;
        this.gui = gui;
    }

    public Player player() {
        return player;
    }

    public AbyssalGui gui() {
        return gui;
    }

    public InventoryCloseEvent event() {
        return event;
    }
}
