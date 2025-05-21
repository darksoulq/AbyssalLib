package com.github.darksoulq.abyssallib.gui.slot;

import com.github.darksoulq.abyssallib.gui.AbstractGui;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds and manages {@link Slot} instances for a specific GUI.
 * Supports separation between top (GUI) and bottom (player) inventory sections.
 */
public class SlotHolder {

    /**
     * Slots registered for the top inventory (GUI-provided inventory).
     */
    public List<Slot> TOP = new ArrayList<>();

    /**
     * Slots registered for the bottom inventory (player inventory).
     */
    public List<Slot> BOTTOM = new ArrayList<>();

    /**
     * Adds a slot to the appropriate inventory section.
     *
     * @param type the inventory section type (TOP or BOTTOM)
     * @param slot the slot to add
     */
    public void add(AbstractGui.Type type, Slot slot) {
        if (type.equals(AbstractGui.Type.TOP)) {
            TOP.add(slot);
        } else if (type.equals(AbstractGui.Type.BOTTOM)) {
            BOTTOM.add(slot);
        }
    }

    /**
     * Retrieves a slot by its index within the specified section.
     *
     * @param type  the inventory section type
     * @param index the index of the slot
     * @return the slot with the matching index, or null if not found
     */
    public Slot get(AbstractGui.Type type, int index) {
        if (type.equals(AbstractGui.Type.TOP)) {
            for (Slot slot : TOP) {
                if (slot.index == index) {
                    return slot;
                }
            }
        } else if (type.equals(AbstractGui.Type.BOTTOM)) {
            for (Slot slot : BOTTOM) {
                if (slot.index == index) {
                    return slot;
                }
            }
        }
        return null;
    }

    /**
     * Returns all slots in the specified inventory section.
     *
     * @param type the inventory section type
     * @return a list of slots in the section
     */
    public List<Slot> getAll(AbstractGui.Type type) {
        if (type == AbstractGui.Type.BOTTOM) return BOTTOM;
        return TOP;
    }
}
