package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * A static, non-interactive GUI element.
 * <p>
 * This element is used to display an item that cannot be picked up or
 * interacted with by the player. It is ideal for background fillers,
 * info icons, and decorative borders.
 */
public class GuiItem implements GuiElement {

    /** The item stack to be displayed in the GUI slot. */
    private final ItemStack item;

    /**
     * Constructs a new GuiItem.
     *
     * @param item the item to display
     */
    public GuiItem(ItemStack item) {
        this.item = item;
    }

    /**
     * Renders the static item to the view.
     *
     * @param view the current GUI view
     * @param slot the slot index
     * @return the item stack to display
     */
    @Override
    public @Nullable ItemStack render(GuiView view, int slot) {
        return item;
    }

    /**
     * Cancels the click action to prevent the item from being taken.
     *
     * @param ctx the click context
     * @return {@link ActionResult#CANCEL}
     */
    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        return ActionResult.CANCEL;
    }

    /**
     * Cancels the drag action to prevent item placement in this slot.
     *
     * @param ctx the drag context
     * @return {@link ActionResult#CANCEL}
     */
    @Override
    public ActionResult onDrag(GuiDragContext ctx) {
        return ActionResult.CANCEL;
    }

    /**
     * Static factory method to create a GuiItem.
     *
     * @param item the item stack
     * @return a new GuiItem instance
     */
    public static GuiItem of(ItemStack item) {
        return new GuiItem(item);
    }
}