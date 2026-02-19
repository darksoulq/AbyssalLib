package com.github.darksoulq.abyssallib.world.gui.layer;

import com.github.darksoulq.abyssallib.common.util.StructureArray;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import com.github.darksoulq.abyssallib.world.item.Item;

/**
 * A standard implementation of a multi-slot progress bar.
 * <p>
 * This class provides public methods to manipulate the progress bar's fill
 * and additional custom metadata strings for shader/model logic.
 */
public class ProgressBarLayer extends AbstractBarLayer {

    /**
     * Constructs a new ProgressBarLayer.
     *
     * @param origin      The top-left slot position.
     * @param structure   The item templates for the bar.
     * @param orientation The direction progress moves (Horizontal/Vertical).
     * @param progressMax The number of sub-states per item model.
     */
    public ProgressBarLayer(SlotPosition origin, StructureArray<Item> structure, StructureArray.Orientation orientation, int progressMax) {
        super(origin, structure, orientation, progressMax);
    }

    /**
     * Sets a custom state string that will be passed to the item's Custom Model Data.
     * <p>
     * The format used in the item strings will be {@code key=value}.
     *
     * @param key   The state key.
     * @param value The value to assign, or {@code null} to remove the key.
     */
    public void setState(String key, String value) {
        if (value == null) {
            customStates.remove(key);
        } else {
            customStates.put(key, value);
        }
        this.lastFilled = -1; // Force re-render
    }

    /**
     * Retrieves the current value of a custom state key.
     *
     * @param key The state key.
     * @return The current value, or null if not set.
     */
    public String getState(String key) {
        return customStates.get(key);
    }
}