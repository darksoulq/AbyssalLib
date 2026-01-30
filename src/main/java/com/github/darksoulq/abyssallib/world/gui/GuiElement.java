package com.github.darksoulq.abyssallib.world.gui;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Represents an interactive component within a GUI.
 * <p>
 * Elements are responsible for their own visual representation (rendering)
 * and handling user interactions such as clicking and dragging.
 */
public interface GuiElement {

    /**
     * Determines the visual representation of this element for a specific slot.
     *
     * @param view the active GUI view
     * @param slot the slot index being rendered
     * @return the ItemStack to display, or null for an empty slot
     */
    @Nullable
    ItemStack render(GuiView view, int slot);

    /**
     * Handles a player clicking on this element.
     *
     * @param view    the active GUI view
     * @param slot    the slot index that was clicked
     * @param click   the type of click performed
     * @param cursor  the item currently held on the cursor, if any
     * @param current the item currently in the clicked slot, if any
     * @return the result of the action, determining if the event should be cancelled
     */
    default ActionResult onClick(GuiView view, int slot, ClickType click, @Nullable ItemStack cursor, @Nullable ItemStack current) {
        return ActionResult.PASS;
    }

    /**
     * Handles items being dragged across slots containing this element.
     *
     * @param view       the active GUI view
     * @param addedItems a map of slot indices to the item stacks being placed
     * @return the result of the action
     */
    default ActionResult onDrag(GuiView view, Map<Integer, ItemStack> addedItems) {
        return ActionResult.PASS;
    }
}