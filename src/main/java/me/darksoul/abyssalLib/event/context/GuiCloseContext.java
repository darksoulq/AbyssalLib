package me.darksoul.abyssalLib.event.context;

import me.darksoul.abyssalLib.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiCloseContext {
    private final Player player;
    private final InventoryCloseEvent event;
    private final AbstractGui gui;

    public GuiCloseContext(AbstractGui gui, InventoryCloseEvent event) {
        this.player = (Player) event.getPlayer();
        this.event = event;
        this.gui = gui;
    }

    public Player player() {
        return player;
    }

    public AbstractGui gui() {
        return gui;
    }

    public InventoryCloseEvent event() {
        return event;
    }
}
