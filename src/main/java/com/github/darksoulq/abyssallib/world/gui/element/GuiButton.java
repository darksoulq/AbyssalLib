package com.github.darksoulq.abyssallib.world.gui.element;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * An interactive GUI element that executes an action when clicked.
 * <p>
 * This element displays an item but executes a provided consumer logic
 * when a player interacts with it. It is the primary way to create
 * clickable controls in a GUI. (Item may be null)
 */
public class GuiButton extends GuiItem {

    /** The action to execute upon clicking the button. */
    private final Consumer<GuiClickContext> action;

    /**
     * Constructs a new GuiButton.
     *
     * @param item   the button icon
     * @param action the logic to run on click
     */
    public GuiButton(ItemStack item, Consumer<GuiClickContext> action) {
        super(item);
        this.action = action;
    }

    /**
     * Executes the button action and cancels the inventory event.
     *
     * @param ctx the click context
     * @return {@link ActionResult#CANCEL}
     */
    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        action.accept(ctx);
        return ActionResult.CANCEL;
    }

    /**
     * Static factory method to create a GuiButton.
     *
     * @param item   the item icon
     * @param action the click logic
     * @return a new GuiButton instance
     */
    public static GuiButton of(ItemStack item, Consumer<GuiClickContext> action) {
        return new GuiButton(item, action);
    }
}