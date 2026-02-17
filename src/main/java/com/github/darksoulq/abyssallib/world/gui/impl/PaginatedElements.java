package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;

import java.util.List;
import java.util.function.Predicate;

/**
 * A visual layer that facilitates pagination of multiple GUI elements across a defined set of slots.
 * <p>
 * This layer maps a large source list of {@link GuiElement}s to a limited number of
 * inventory slots. It supports dynamic filtering and provides methods to navigate
 * through pages of content.
 *
 * @deprecated use {@link PagedLayer}
 */
@Deprecated(since = "v2.0.0-mc1.21.11-dev.3", forRemoval = true)
public class PaginatedElements extends PagedLayer<GuiElement> {

    /**
     * Constructs a new PaginatedElements layer.
     *
     * @param source  the full list of elements to paginate
     * @param slots   the array of slot IDs where elements will be placed
     * @param segment the inventory segment to render into
     */
    public PaginatedElements(List<GuiElement> source, int[] slots, GuiView.Segment segment) {
        super(source, slots, segment);
    }

    /**
     * Updates the filter and resets the pagination state to the first page.
     *
     * @param filter the new predicate to apply to the source elements
     */
    @Override
    public void setFilter(Predicate<GuiElement> filter) {
        super.setFilter(filter);
    }

    /**
     * Advances the view to the next available page, wrapping around to the start if necessary.
     *
     * @param view the active GUI view
     */
    public void next(GuiView view) {
        super.next(view);
    }

    /**
     * Moves the view to the previous page, wrapping around to the end if necessary.
     *
     * @param view the active GUI view
     */
    public void prev(GuiView view) {
        super.previous(view);
    }

    /**
     * Calculates the total number of pages based on the current filtered list size.
     *
     * @return the total page count
     */
    public int pageCount() {
        return super.getPageCount();
    }

    /**
     * Resets the render tracker, forcing a re-render on the next tick.
     */
    public void resetPage() {
        super.invalidate();
    }
}