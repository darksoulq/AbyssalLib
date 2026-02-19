package com.github.darksoulq.abyssallib.world.gui.element;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * A GUI element that cycles through a list of predefined states when clicked.
 * <p>
 * This element supports multiple states, each associated with an icon and a value.
 * Left-clicking cycles forward through the list, while right-clicking cycles backward.
 *
 * @param <T> The type of the value associated with each state.
 */
public class StateCycleElement<T> implements GuiElement {

    /** The list of possible states for this element. */
    private final List<State<T>> states;

    /** The callback executed whenever the current state index changes. */
    private final Consumer<T> onChange;

    /** The index of the currently active state in the list. */
    private int currentIndex;

    /**
     * Constructs a new StateCycleElement.
     *
     * @param states       A list of {@link State} objects representing the cycle.
     * @param initialIndex The index of the state to start on.
     * @param onChange     A consumer notified when a new state value is selected.
     * @throws IllegalArgumentException If the provided states list is empty.
     */
    public StateCycleElement(List<State<T>> states, int initialIndex, Consumer<T> onChange) {
        if (states.isEmpty()) throw new IllegalArgumentException("States list cannot be empty");
        this.states = states;
        this.currentIndex = Math.max(0, Math.min(initialIndex, states.size() - 1));
        this.onChange = onChange;
    }

    /**
     * Renders the icon of the current state.
     *
     * @param view The active GUI view.
     * @param slot The slot index where the element is rendered.
     * @return The {@link ItemStack} for the current index.
     */
    @Override
    public @Nullable ItemStack render(GuiView view, int slot) {
        return states.get(currentIndex).icon;
    }

    /**
     * Handles the click interaction to cycle through states.
     * <p>
     * A Right-Click decrements the index (cycles backward), while any other
     * click type increments the index (cycles forward).
     *
     * @param ctx The context of the click event.
     * @return {@link ActionResult#CANCEL} to prevent the player from taking the icon.
     */
    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        if (ctx.clickType().isRightClick()) {
            currentIndex = (currentIndex - 1 + states.size()) % states.size();
        } else {
            currentIndex = (currentIndex + 1) % states.size();
        }
        onChange.accept(states.get(currentIndex).value);
        return ActionResult.CANCEL;
    }

    /**
     * Prevents items from being dragged onto this cycle element.
     *
     * @param ctx The context of the drag event.
     * @return {@link ActionResult#CANCEL}.
     */
    @Override
    public ActionResult onDrag(GuiDragContext ctx) {
        return ActionResult.CANCEL;
    }

    /**
     * Gets the value of the currently active state.
     *
     * @return The current state value.
     */
    public T getCurrentValue() {
        return states.get(currentIndex).value;
    }

    /**
     * Represents a single state in the cycle.
     *
     * @param <T>   The value type.
     * @param icon  The item to display for this state.
     * @param value The logic value represented by this state.
     */
    public record State<T>(ItemStack icon, T value) {}

    /**
     * Static factory method to create a StateCycleElement.
     *
     * @param <T>          The value type.
     * @param states       The list of states.
     * @param initialIndex The starting index.
     * @param onChange     The change listener.
     * @return A new StateCycleElement instance.
     */
    public static <T> StateCycleElement<T> of(List<State<T>> states, int initialIndex, Consumer<T> onChange) {
        return new StateCycleElement<>(states, initialIndex, onChange);
    }
}