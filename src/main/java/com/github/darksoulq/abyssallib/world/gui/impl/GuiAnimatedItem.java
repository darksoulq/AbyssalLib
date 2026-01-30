package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * A dynamic GUI element that renders an animated item based on the server's tick count.
 * <p>
 * This class utilizes a {@link BiFunction} to determine the visual state of the element
 * at any given moment. It is ideal for progress bars, flashing icons, or rotating
 * displays. Like {@link GuiItem}, it prevents players from manipulating the item.
 */
public class GuiAnimatedItem implements GuiElement {

    /**
     * The renderer function that produces an ItemStack.
     * <p>
     * The function accepts the current {@link GuiView} and the current server tick
     * integer, returning the {@link ItemStack} to be displayed for that frame.
     */
    private final BiFunction<GuiView, Integer, ItemStack> renderer;

    /**
     * Constructs a new GuiAnimatedItem with the specified animation renderer.
     *
     * @param renderer a function taking the view and tick count to produce an item
     */
    public GuiAnimatedItem(BiFunction<GuiView, Integer, ItemStack> renderer) {
        this.renderer = renderer;
    }

    /**
     * Renders the current state of the animation to the GUI slot.
     * <p>
     * This method retrieves the current server tick from {@link Bukkit#getCurrentTick()}
     * and passes it to the internal renderer function.
     *
     * @param view the active GUI view
     * @param slot the slot index being rendered
     * @return the item stack produced by the renderer for the current tick
     */
    @Override
    public ItemStack render(GuiView view, int slot) {
        int tick = Bukkit.getCurrentTick();
        return renderer.apply(view, tick);
    }

    /**
     * Handles click interactions by cancelling them.
     *
     * @param view    the active GUI view
     * @param slot    the clicked slot index
     * @param click   the type of click performed
     * @param cursor  the item on the player's cursor
     * @param current the item currently in the slot
     * @return {@link ActionResult#CANCEL} to ensure the animation remains in place
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
     * Static factory method to create a GuiAnimatedItem.
     *
     * @param renderer the function defining the animation logic
     * @return a new GuiAnimatedItem instance
     */
    public static GuiAnimatedItem of(BiFunction<GuiView, Integer, ItemStack> renderer) {
        return new GuiAnimatedItem(renderer);
    }
}