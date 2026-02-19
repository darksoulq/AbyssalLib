package com.github.darksoulq.abyssallib.world.gui.element;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A GUI element that toggles between two states (on and off) when clicked.
 * <p>
 * This element maintains a boolean state and displays a corresponding icon for each state.
 * When a player clicks the element, the state is inverted, the icon is updated, and
 * a toggle listener is notified of the change.
 */
public class ToggleElement implements GuiElement {

    /** The item stack to display when the state is {@code true}. */
    private final ItemStack onIcon;

    /** The item stack to display when the state is {@code false}. */
    private final ItemStack offIcon;

    /** The callback executed whenever the state is changed. */
    private final Consumer<Boolean> onToggle;

    /** The current binary state of the element. */
    private boolean state;

    /**
     * Constructs a new ToggleElement.
     *
     * @param onIcon       The icon used for the 'on' state.
     * @param offIcon      The icon used for the 'off' state.
     * @param initialState The starting state of the toggle.
     * @param onToggle     A consumer that accepts the new state after a toggle occurs.
     */
    public ToggleElement(ItemStack onIcon, ItemStack offIcon, boolean initialState, Consumer<Boolean> onToggle) {
        this.onIcon = onIcon;
        this.offIcon = offIcon;
        this.state = initialState;
        this.onToggle = onToggle;
    }

    /**
     * Renders the appropriate icon based on the current state.
     *
     * @param view The active GUI view.
     * @param slot The slot index where the element is rendered.
     * @return The {@link ItemStack} corresponding to the current state.
     */
    @Override
    public @Nullable ItemStack render(GuiView view, int slot) {
        return state ? onIcon : offIcon;
    }

    /**
     * Handles the click interaction by flipping the current state.
     * <p>
     * After flipping the state, it triggers the {@code onToggle} consumer and
     * cancels the inventory action to keep the icon in the slot.
     *
     * @param ctx The context of the click event.
     * @return {@link ActionResult#CANCEL} to prevent the player from taking the icon.
     */
    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        state = !state;
        onToggle.accept(state);
        return ActionResult.CANCEL;
    }

    /**
     * Prevents items from being dragged onto this toggle element.
     *
     * @param ctx The context of the drag event.
     * @return {@link ActionResult#CANCEL}.
     */
    @Override
    public ActionResult onDrag(GuiDragContext ctx) {
        return ActionResult.CANCEL;
    }

    /**
     * Retrieves the current state of the toggle.
     *
     * @return {@code true} if on, {@code false} if off.
     */
    public boolean getState() {
        return state;
    }

    /**
     * Manually updates the state of the toggle.
     *
     * @param state The new state to set.
     */
    public void setState(boolean state) {
        this.state = state;
    }

    /**
     * Static factory method to create a ToggleElement.
     *
     * @param onIcon       The 'on' icon.
     * @param offIcon      The 'off' icon.
     * @param initialState The initial state.
     * @param onToggle     The toggle handler.
     * @return A new ToggleElement instance.
     */
    public static ToggleElement of(ItemStack onIcon, ItemStack offIcon, boolean initialState, Consumer<Boolean> onToggle) {
        return new ToggleElement(onIcon, offIcon, initialState, onToggle);
    }
}