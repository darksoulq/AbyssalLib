package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.world.gui.*;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A visual layer that facilitates pagination of multiple GUI elements across a defined set of slots.
 * <p>
 * This layer maps a large source list of {@link GuiElement}s to a limited number of
 * inventory slots. It supports dynamic filtering and provides methods to navigate
 * through pages of content.
 */
public class PaginatedElements implements GuiLayer {
    /** The specific inventory slot indices available for rendering elements. */
    private final int[] slots;

    /** The target inventory segment (TOP or BOTTOM). */
    private final GuiView.Segment segment;

    /** The master list of all potential elements. */
    private final List<GuiElement> source;

    /** The current subset of elements that satisfy the active filter. */
    private List<GuiElement> filtered;

    /** The predicate used to determine which elements should be displayed. */
    private Predicate<GuiElement> filter = el -> true;

    /** The current page index (0-based). */
    private int page = 0;

    /** Tracks the last rendered page to avoid redundant updates. */
    private int lastRenderedPage = -1;

    /**
     * Constructs a new PaginatedElements layer.
     *
     * @param source  the full list of elements to paginate
     * @param slots   the array of slot IDs where elements will be placed
     * @param segment the inventory segment to render into
     */
    public PaginatedElements(List<GuiElement> source, int[] slots, GuiView.Segment segment) {
        this.source = new ArrayList<>(source);
        this.slots = slots;
        this.segment = segment;
        this.filtered = source.stream().filter(filter).toList();
    }

    /**
     * Updates the filter and resets the pagination state to the first page.
     *
     * @param filter the new predicate to apply to the source elements
     */
    public void setFilter(Predicate<GuiElement> filter) {
        this.filter = filter;
        this.filtered = source.stream().filter(filter).toList();
        this.page = 0;
        this.lastRenderedPage = -1;
    }

    /**
     * Advances the view to the next available page, wrapping around to the start if necessary.
     *
     * @param view the active GUI view
     */
    public void next(GuiView view) {
        if (pageCount() <= 0) return;
        int newPage = (page + 1) % pageCount();
        if (newPage != page) {
            page = newPage;
            cleanup(view);
        }
    }

    /**
     * Moves the view to the previous page, wrapping around to the end if necessary.
     *
     * @param view the active GUI view
     */
    public void prev(GuiView view) {
        if (pageCount() <= 0) return;
        int newPage = (page - 1 + pageCount()) % pageCount();
        if (newPage != page) {
            page = newPage;
            cleanup(view);
        }
    }

    /**
     * Renders the current page of elements to the specified GUI view.
     * <p>
     * This method calculates the start index in the filtered list based on the
     * current page and assigns elements to the mapped slots in the GUI.
     *
     * @param view the active view to render into
     */
    @Override
    public void renderTo(GuiView view) {
        if (page == lastRenderedPage) return;
        cleanup(view);

        Gui gui = view.getGui();
        int start = page * slots.length;

        for (int i = 0; i < slots.length; i++) {
            int global = start + i;
            if (global >= filtered.size()) break;

            int slot = slots[i];
            GuiElement el = filtered.get(global);
            SlotPosition pos = new SlotPosition(segment, slot);

            gui.getElements().put(pos, el);
        }
        lastRenderedPage = page;
    }

    /**
     * Removes the paginated elements from the GUI and clears the items from the inventory.
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
     * Calculates the total number of pages based on the current filtered list size.
     *
     * @return the total page count
     */
    public int pageCount() {
        return (int) Math.ceil((double) filtered.size() / slots.length);
    }

    /**
     * Gets the current page index.
     *
     * @return the current page
     */
    public int getPage() {
        return page;
    }

    /**
     * Resets the render tracker, forcing a re-render on the next tick.
     */
    public void resetPage() {
        lastRenderedPage = -1;
    }
}