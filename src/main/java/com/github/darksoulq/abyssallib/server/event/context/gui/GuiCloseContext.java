package com.github.darksoulq.abyssallib.server.event.context.gui;

import com.github.darksoulq.abyssallib.server.event.context.Context;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Represents the context of a GUI close event, capturing details such as the player
 * and the associated GUI instance that was closed.
 * <p>
 * This class provides access to the player who closed the GUI and the GUI itself
 * that was closed, allowing handlers to manage actions upon the GUI being closed.
 * </p>
 */
public class GuiCloseContext extends Context<InventoryCloseEvent> {
    /**
     * The player who closed the GUI.
     */
    public final Player player;
    /**
     * The GUI instance that was closed.
     */
    public final AbstractGui gui;

    /**
     * Constructs a new GuiCloseContext with the given AbstractGui and InventoryCloseEvent.
     * Initializes all the fields using the data from the event.
     *
     * @param gui   The GUI instance that was closed.
     * @param event The InventoryCloseEvent associated with this context.
     */
    public GuiCloseContext(AbstractGui gui, InventoryCloseEvent event) {
        super(event);
        this.player = (Player) event.getPlayer();
        this.gui = gui;
    }
}
