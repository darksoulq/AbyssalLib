package com.github.darksoulq.abyssallib.server.event.context.gui;

import com.github.darksoulq.abyssallib.server.event.context.Cancellable;
import com.github.darksoulq.abyssallib.server.event.context.Context;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents the context of a GUI click event, capturing details such as the player,
 * the clicked item, the slot, the click type, and the associated GUI.
 * <p>
 * This class allows access to the event details that are useful for handling inventory
 * click interactions within a GUI, including the item being clicked, the player's action,
 * and the specific slot involved.
 * </p>
 */
public class GuiClickContext extends Context<InventoryClickEvent> implements Cancellable {
    /**
     * The player who clicked in the inventory.
     */
    public final Player player;
    /**
     * The item clicked in the inventory.
     */
    public final ItemStack clickedItem;
    /**
     * The slot where the item was clicked.
     */
    public final int slot;
    /**
     * The raw slot.
     */
    public final int rawSlot;
    /**
     * The type of click performed (e.g., left-click, right-click).
     */
    public final ClickType clickType;
    /**
     * The GUI instance in which the click occurred.
     */
    public final AbstractGui gui;

    /**
     * Constructs a new GuiClickContext with the given AbstractGui and InventoryClickEvent.
     * Initializes all the fields using the data from the event.
     *
     * @param gui   The GUI instance where the click occurred.
     * @param event The InventoryClickEvent associated with this context.
     */
    public GuiClickContext(AbstractGui gui, InventoryClickEvent event) {
        super(event);
        this.gui = gui;
        this.player = (Player) event.getWhoClicked();
        this.clickedItem = event.getCurrentItem();
        this.slot = event.getSlot();
        this.rawSlot = event.getRawSlot();
        this.clickType = event.getClick();
    }

    /**
     * Cancels the inventory click event, preventing any subsequent actions.
     */
    public void cancel() {
        event.setCancelled(true);
    }
}
