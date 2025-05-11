package com.github.darksoulq.abyssallib.event.context.gui;

import com.github.darksoulq.abyssallib.event.context.Cancellable;
import com.github.darksoulq.abyssallib.event.context.Context;
import com.github.darksoulq.abyssallib.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;


/**
 * Represents the context of a GUI drag event, containing details about the player,
 * the GUI being interacted with, and the dragged items.
 * <p>
 * This class provides access to the player who is dragging items, the GUI involved in
 * the event, and allows cancellation of the event if needed.
 * </p>
 */
public class GuiDragContext extends Context<InventoryDragEvent> implements Cancellable {
    /**
     * The player who is interacting with the GUI.
     */
    public final Player player;
    /**
     * The GUI instance where the drag event occurred.
     */
    public final AbstractGui gui;

    /**
     * Constructs a new GuiDragContext with the given AbstractGui and InventoryDragEvent.
     * Initializes the context using the data from the event.
     *
     * @param gui   The GUI instance where the drag event occurred.
     * @param event The InventoryDragEvent associated with this context.
     */
    public GuiDragContext(AbstractGui gui, InventoryDragEvent event) {
        super(event);
        this.player = (Player) event.getWhoClicked();
        this.gui = gui;
    }

    /**
     * Cancels the drag event, preventing it from being processed further.
     */
    public void cancel() {
        event.setCancelled(true);
    }
}
