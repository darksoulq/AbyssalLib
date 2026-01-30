package com.github.darksoulq.abyssallib.world.gui;

/**
 * A record defining a specific location within a GuiView.
 * <p>
 * Combines an inventory segment with a numerical slot index to uniquely
 * identify a slot in either the top or bottom inventory.
 *
 * @param segment the inventory segment (TOP or BOTTOM)
 * @param index   the slot index within that segment
 */
public record SlotPosition(GuiView.Segment segment, int index) {

    /**
     * Creates a position reference for the top inventory.
     *
     * @param slot the slot index
     * @return a new SlotPosition for the top segment
     */
    public static SlotPosition top(int slot) {
        return new SlotPosition(GuiView.Segment.TOP, slot);
    }

    /**
     * Creates a position reference for the bottom inventory.
     *
     * @param slot the slot index
     * @return a new SlotPosition for the bottom segment
     */
    public static SlotPosition bottom(int slot) {
        return new SlotPosition(GuiView.Segment.BOTTOM, slot);
    }
}