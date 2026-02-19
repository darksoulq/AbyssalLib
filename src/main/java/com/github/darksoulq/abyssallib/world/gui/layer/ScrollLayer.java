package com.github.darksoulq.abyssallib.world.gui.layer;

import com.github.darksoulq.abyssallib.world.gui.Gui;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A GUI layer that facilitates a scrollable view of generic data objects.
 * <p>
 * Unlike pagination, which jumps between fixed groups of elements, the ScrollLayer
 * allows for a dynamic offset within the source list. This creates a "window" that
 * can slide through the data one step at a time.
 *
 * @param <T> The type of data being scrolled.
 */
public class ScrollLayer<T> implements GuiLayer {

    /** The master list of data objects to be displayed. */
    protected final List<T> source;

    /** Function to map data objects to interactive GUI elements based on their global index. */
    protected final BiFunction<T, Integer, GuiElement> mapper;

    /** The specific inventory slot indices available for the scrollable content. */
    protected final int[] slots;

    /** The target inventory segment (TOP or BOTTOM). */
    protected final GuiView.Segment segment;

    /** The number of elements to jump forward or backward during a scroll action. */
    protected final int scrollStep;

    /** The current starting index in the source list for rendering. */
    protected int scrollOffset = 0;

    /** Tracks the last rendered offset to optimize updates and prevent redundant logic. */
    protected int lastRenderedOffset = -1;

    /**
     * Constructs a new ScrollLayer.
     *
     * @param source     The full list of data to display.
     * @param slots      The array of slot indices used for rendering the window.
     * @param segment    The inventory segment where slots are located.
     * @param scrollStep The number of indices to shift during scrollUp/scrollDown.
     * @param mapper     The function to transform data into GUI elements.
     */
    public ScrollLayer(List<T> source, int[] slots, GuiView.Segment segment, int scrollStep, BiFunction<T, Integer, GuiElement> mapper) {
        this.source = new ArrayList<>(source);
        this.slots = slots;
        this.segment = segment;
        this.scrollStep = Math.max(1, scrollStep);
        this.mapper = mapper;
    }

    /**
     * Shifts the view window downward by the defined scroll step.
     * <p>
     * The offset is clamped to the maximum possible scrollable index based on the
     * source list size and the available slot count.
     *
     * @param view The active GUI view context.
     */
    public void scrollDown(GuiView view) {
        int maxOffset = Math.max(0, source.size() - slots.length);
        int nextOffset = Math.min(scrollOffset + scrollStep, maxOffset);
        if (nextOffset != scrollOffset) {
            scrollOffset = nextOffset;
            cleanup(view);
        }
    }

    /**
     * Shifts the view window upward by the defined scroll step.
     * <p>
     * The offset is clamped to a minimum of 0.
     *
     * @param view The active GUI view context.
     */
    public void scrollUp(GuiView view) {
        int prevOffset = Math.max(0, scrollOffset - scrollStep);
        if (prevOffset != scrollOffset) {
            scrollOffset = prevOffset;
            cleanup(view);
        }
    }

    /**
     * Sets the scroll offset to a specific index.
     * <p>
     * The provided offset is validated to ensure it falls within the range of
     * 0 to the maximum possible scroll index.
     *
     * @param view   The active GUI view context.
     * @param offset The new target scroll index.
     */
    public void setScroll(GuiView view, int offset) {
        int maxOffset = Math.max(0, source.size() - slots.length);
        int validOffset = Math.max(0, Math.min(offset, maxOffset));
        if (validOffset != scrollOffset) {
            scrollOffset = validOffset;
            cleanup(view);
        }
    }

    /**
     * Renders the current "window" of the data list to the inventory view.
     * <p>
     * It maps data from {@code source[scrollOffset]} to {@code source[scrollOffset + slots.length]}
     * and places the resulting elements into the defined slots.
     *
     * @param view The active view instance to render into.
     */
    @Override
    public void renderTo(GuiView view) {
        if (scrollOffset == lastRenderedOffset) return;
        cleanup(view);

        Gui gui = view.getGui();

        for (int i = 0; i < slots.length; i++) {
            int globalIndex = scrollOffset + i;
            if (globalIndex >= source.size()) break;

            int slotIndex = slots[i];
            T data = source.get(globalIndex);
            GuiElement element = mapper.apply(data, globalIndex);

            gui.getElements().put(new SlotPosition(segment, slotIndex), element);
        }
        lastRenderedOffset = scrollOffset;
    }

    /**
     * Cleans up the current scroll slots by removing element mappings and
     * clearing the item stacks from the actual inventory.
     *
     * @param view The view instance being cleaned up.
     */
    @Override
    public void cleanup(GuiView view) {
        Inventory inv = segment == GuiView.Segment.TOP ? view.getTop() : view.getBottom();
        for (int slot : slots) {
            view.getGui().getElements().remove(new SlotPosition(segment, slot));
            inv.setItem(slot, null);
        }
    }

    /**
     * Gets the current scroll offset.
     *
     * @return The current starting index of the rendered window.
     */
    public int getScrollOffset() {
        return scrollOffset;
    }

    /**
     * Calculates the maximum valid scroll offset.
     *
     * @return The highest index that can be at the start of the window.
     */
    public int getMaxScroll() {
        return Math.max(0, source.size() - slots.length);
    }

    /**
     * Invalidates the render cache, forcing the layer to re-render on the next tick.
     */
    public void invalidate() {
        lastRenderedOffset = -1;
    }
}