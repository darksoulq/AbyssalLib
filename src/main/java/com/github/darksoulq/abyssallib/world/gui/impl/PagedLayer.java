package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.world.gui.*;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * A GUI layer designed for paginating generic data objects into GUI elements.
 * <p>
 * This layer manages a source list of data, applies filtering logic, and
 * maps a specific "window" of that data to inventory slots based on the current page.
 *
 * @param <T> the type of data being paginated
 */
public class PagedLayer<T> implements GuiLayer {

    /** The master source of data to be paginated. */
    protected final List<T> source;

    /** Function to map data objects to interactive GUI elements. */
    protected final BiFunction<T, Integer, GuiElement> mapper;

    /** The specific slot indices available for content rendering. */
    protected final int[] slots;

    /** The inventory segment target (TOP or BOTTOM). */
    protected final GuiView.Segment segment;

    /** The current subset of data passing the active filter. */
    protected List<T> filtered;

    /** The active filter for data visibility. */
    protected Predicate<T> filter = t -> true;

    /** The current page index (0-based). */
    protected int page = 0;

    /** Tracks the last rendered page index. */
    protected int lastRenderedPage = -1;

    /**
     * Constructs a PagedLayer with a custom data-to-element mapper.
     *
     * @param source  the master data list
     * @param slots   the target slots for items
     * @param segment the target inventory segment
     * @param mapper  the function creating elements from data
     */
    public PagedLayer(List<T> source, int[] slots, GuiView.Segment segment, BiFunction<T, Integer, GuiElement> mapper) {
        this.source = new ArrayList<>(source);
        this.slots = slots;
        this.segment = segment;
        this.mapper = mapper;
        this.filtered = new ArrayList<>(this.source);
    }

    /**
     * Constructs a PagedLayer for direct GuiElement pagination.
     *
     * @param source  the list of GUI elements
     * @param slots   the target slots for items
     * @param segment the target inventory segment
     */
    public PagedLayer(List<T> source, int[] slots, GuiView.Segment segment) {
        this(source, slots, segment, (t, i) -> (GuiElement) t);
    }

    /**
     * Updates the data filter and resets the view to the first page.
     *
     * @param filter the data predicate
     */
    public void setFilter(Predicate<T> filter) {
        this.filter = filter;
        this.filtered = source.stream().filter(filter).toList();
        this.page = 0;
        this.lastRenderedPage = -1;
    }

    /**
     * Advances to the next page of content.
     *
     * @param view the active GUI view context
     */
    public void next(GuiView view) {
        if (getPageCount() <= 0) return;
        int next = (page + 1) % getPageCount();
        if (next != page) {
            page = next;
            cleanup(view);
        }
    }

    /**
     * Returns to the previous page of content.
     *
     * @param view the active GUI view context
     */
    public void previous(GuiView view) {
        if (getPageCount() <= 0) return;
        int prev = (page - 1 + getPageCount()) % getPageCount();
        if (prev != page) {
            page = prev;
            cleanup(view);
        }
    }

    /**
     * Renders the current page of data as GUI elements.
     * <p>
     * Calculations are performed to slice the filtered data list and
     * map results to the configured slot indices.
     *
     * @param view the view instance to render into
     */
    @Override
    public void renderTo(GuiView view) {
        if (page == lastRenderedPage) return;
        cleanup(view);

        Gui gui = view.getGui();
        int start = page * slots.length;

        for (int i = 0; i < slots.length; i++) {
            int globalIndex = start + i;
            if (globalIndex >= filtered.size()) break;

            int slotIndex = slots[i];
            T data = filtered.get(globalIndex);
            GuiElement element = mapper.apply(data, globalIndex);

            gui.getElements().put(new SlotPosition(segment, slotIndex), element);
        }
        lastRenderedPage = page;
    }

    /**
     * Cleans up the rendered items and removed mapped elements.
     *
     * @param view the view instance being cleaned up
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
     * Calculates the total number of pages based on filtered data size.
     *
     * @return the total page count
     */
    public int getPageCount() {
        return (int) Math.ceil((double) filtered.size() / slots.length);
    }

    /**
     * Gets the current page index.
     *
     * @return current page
     */
    public int getPage() {
        return page;
    }

    /**
     * Forces the layer to re-render.
     */
    public void invalidate() {
        lastRenderedPage = -1;
    }

    /**
     * Static helper for creating a generic PagedLayer.
     *
     * @param <T>     data type
     * @param source  data source
     * @param slots   target slots
     * @param segment target segment
     * @param mapper  mapping function
     * @return a new PagedLayer
     */
    public static <T> PagedLayer<T> of(List<T> source, int[] slots, GuiView.Segment segment, BiFunction<T, Integer, GuiElement> mapper) {
        return new PagedLayer<>(source, slots, segment, mapper);
    }

    /**
     * Static helper for creating an element-specific PagedLayer.
     *
     * @param elements list of GUI elements
     * @param slots    target slots
     * @param segment  target segment
     * @return a new PagedLayer for GUI elements
     */
    public static PagedLayer<GuiElement> of(List<GuiElement> elements, int[] slots, GuiView.Segment segment) {
        return new PagedLayer<>(elements, slots, segment);
    }
}