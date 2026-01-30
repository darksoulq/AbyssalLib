package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A simple, non-interactive GUI element that displays a static item.
 * <p>
 * This implementation is typically used for background decoration,
 * menu borders, or informational icons. It cancels all click and
 * drag actions to prevent the item from being manipulated by players.
 */
public class GuiItem implements GuiElement {

    /** The item stack to be displayed in the GUI slot. */
    private final ItemStack item;

    /**
     * Constructs a new GuiItem with the specified item stack.
     *
     * @param item the item to display
     */
    public GuiItem(ItemStack item) {
        this.item = item;
    }

    /**
     * Renders the static item to the specified slot.
     *
     * @param view the active GUI view
     * @param slot the slot index being rendered
     * @return the item stack assigned to this element
     */
    @Override
    public ItemStack render(GuiView view, int slot) {
        return item;
    }

    /**
     * Handles click interactions by cancelling them.
     * <p>
     * This prevents players from taking the item out of the GUI
     * or placing items into the slot.
     *
     * @param view    the active GUI view
     * @param slot    the clicked slot index
     * @param click   the type of click performed
     * @param cursor  the item on the player's cursor
     * @param current the item currently in the slot
     * @return {@link ActionResult#CANCEL} to block the interaction
     */
    @Override
    public ActionResult onClick(GuiView view, int slot, ClickType click, @Nullable ItemStack cursor, @Nullable ItemStack current) {
        return ActionResult.CANCEL;
    }

    /**
     * Handles drag interactions by cancelling them.
     *
     * @param view       the active GUI view
     * @param addedItems the map of items being dragged into slots
     * @return {@link ActionResult#CANCEL} to block the drag operation
     */
    @Override
    public ActionResult onDrag(GuiView view, Map<Integer, ItemStack> addedItems) {
        return ActionResult.CANCEL;
    }

    /**
     * Static factory method to create a GuiItem.
     *
     * @param item the item stack to display
     * @return a new GuiItem instance
     */
    public static GuiItem of(ItemStack item) {
        return new GuiItem(item);
    }
}