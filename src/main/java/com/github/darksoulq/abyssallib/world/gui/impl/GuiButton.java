package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * An interactive GUI element that executes a callback when clicked.
 * <p>
 * This element displays a static {@link ItemStack} but allows for functional
 * logic to be triggered via a {@link BiConsumer}. Like {@link GuiItem}, it
 * cancels the default inventory action to ensure the button remains in the slot.
 */
public class GuiButton implements GuiElement {

    /** The item stack to be displayed as the button icon. */
    private final ItemStack item;

    /** The logic to execute when the button is clicked. */
    private final BiConsumer<GuiView, ClickType> onClick;

    /**
     * Constructs a new GuiButton with an icon and a click handler.
     *
     * @param item    the item to display
     * @param onClick the consumer accepting the active view and the click type
     */
    public GuiButton(ItemStack item, BiConsumer<GuiView, ClickType> onClick) {
        this.item = item;
        this.onClick = onClick;
    }

    /**
     * Renders the button's item to the specified slot.
     *
     * @param view the active GUI view
     * @param slot the slot index being rendered
     * @return the item stack assigned to this button
     */
    @Override
    public ItemStack render(GuiView view, int slot) {
        return item;
    }

    /**
     * Handles the click event by executing the assigned callback.
     * <p>
     * The default inventory behavior is cancelled to prevent the player
     * from picking up the button.
     *
     * @param view    the active GUI view
     * @param slot    the clicked slot index
     * @param click   the type of click performed (Left, Right, Shift, etc.)
     * @param cursor  the item on the player's cursor
     * @param current the item currently in the slot
     * @return {@link ActionResult#CANCEL} to prevent item manipulation
     */
    @Override
    public ActionResult onClick(GuiView view, int slot, ClickType click, ItemStack cursor, ItemStack current) {
        onClick.accept(view, click);
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
     * Static factory method to create a GuiButton.
     *
     * @param item    the item stack to display
     * @param onClick the consumer defining the button's behavior
     * @return a new GuiButton instance
     */
    public static GuiButton of(ItemStack item, BiConsumer<GuiView, ClickType> onClick) {
        return new GuiButton(item, onClick);
    }
}