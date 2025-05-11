package com.github.darksoulq.abyssallib.gui.slot;

import com.github.darksoulq.abyssallib.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.gui.AbstractGui;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a slot within a GUI.
 * <p>
 * This is an abstract base class, and different types of slots should extend this class
 * to provide specific behavior, such as handling item insertion and clicks.
 */
public abstract class Slot {
    protected final int index;

    /**
     * Constructs a new slot with a specified index.
     *
     * @param index the index of the slot in the inventory
     */
    public Slot(int index) {
        this.index = index;
    }

    /**
     * Determines if this slot should be serialized.
     * Can be overridden to change serialization behavior.
     *
     * @return true if this slot should be serialized, false otherwise
     */
    public boolean doSerialize() {
        return true;
    }

    /**
     * Retrieves the current item in the slot.
     *
     * @return the {@link ItemStack} currently in this slot
     */
    public abstract ItemStack item();
    /**
     * Attempts to set a new item for this slot.
     *
     * @param item the {@link ItemStack} to set in this slot
     */
    public abstract void item(ItemStack item);
    /**
     * Determines if a given {@link ItemStack} can be inserted into this slot.
     *
     * @param stack the item stack to check for insertion
     * @return true if the item can be inserted, false otherwise
     */
    public abstract boolean canInsert(ItemStack stack);
    /**
     * Handles a click event inside the slot.
     * This method should be overridden to define custom behavior on clicks.
     *
     * @param ctx the context of the click event
     */
    public abstract void onClick(GuiClickContext ctx);
    /**
     * Called on each tick of the GUI to update slot behavior.
     * This method can be overridden to implement custom ticking behavior for the slot.
     *
     * @param gui the GUI containing this slot
     */
    public abstract void onTick(AbstractGui gui);
    /**
     * Returns the index of this slot in the inventory.
     *
     * @return the index of the slot
     */
    public int index() {
        return index;
    }
}
