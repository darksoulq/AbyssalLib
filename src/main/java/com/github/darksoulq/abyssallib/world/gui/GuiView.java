package com.github.darksoulq.abyssallib.world.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an active instance of a {@link Gui} being viewed by a player.
 * <p>
 * This class handles the runtime rendering of elements and layers, and
 * provides access to both the top (GUI) and bottom (Player) inventory segments.
 */
public class GuiView {
    /**
     * Defines the target inventory segment for rendering or interaction.
     */
    public enum Segment {
        /** The custom GUI inventory. */
        TOP,
        /** The viewing player's inventory. */
        BOTTOM
    }

    /** The underlying configuration for this view. */
    private final Gui gui;

    /** The Bukkit InventoryView connecting the player to the inventories. */
    private final InventoryView view;

    /**
     * Constructs a new GuiView instance.
     *
     * @param gui  the gui template
     * @param view the active inventory view
     */
    public GuiView(Gui gui, InventoryView view) {
        this.gui = gui;
        this.view = view;
    }

    /**
     * Gets the GUI template used by this view.
     *
     * @return the gui
     */
    public Gui getGui() {
        return gui;
    }

    /**
     * Gets the Bukkit InventoryView.
     *
     * @return the inventory view
     */
    public InventoryView getInventoryView() {
        return view;
    }

    /**
     * Gets the top inventory segment (the custom GUI).
     *
     * @return the top inventory
     */
    public Inventory getTop() {
        return view.getTopInventory();
    }

    /**
     * Gets the bottom inventory segment (the player's inventory).
     *
     * @return the player inventory
     */
    public Inventory getBottom() {
        return view.getBottomInventory();
    }

    /**
     * Updates the state of the GUI for the current tick.
     * <p>
     * This method renders all active layers, executes ticker logic,
     * and updates slot contents based on the elements defined in the Gui.
     */
    public void tick() {
        Inventory top = getTop();
        Inventory bot = getBottom();
        gui.getLayers().forEach(layer -> layer.renderTo(this));
        gui.getTickers().forEach(t -> t.accept(this));
        gui.getElements().forEach((s, e) -> {
            ItemStack item = e.render(this, s.index());
            if (item != null) {
                if (s.segment() == Segment.TOP) {
                    if (top.getItem(s.index()) != item) {
                        top.setItem(s.index(), item);
                    }
                } else {
                    if (bot.getItem(s.index()) != item) {
                        bot.setItem(s.index(), item);
                    }
                }
            }
        });
    }

    /**
     * Closes the inventory for the specified player and triggers the close handler.
     *
     * @param player the player viewing the GUI
     */
    public void close(HumanEntity player) {
        player.closeInventory();
        gui.getOnClose().accept(this);
    }

    /**
     * Retrieves the element assigned to a specific slot.
     *
     * @param segment the inventory segment
     * @param slot    the slot index
     * @return the GuiElement, or null if empty
     */
    public GuiElement getElementAt(Segment segment, int slot) {
        return gui.getElements().get(new SlotPosition(segment, slot));
    }
}