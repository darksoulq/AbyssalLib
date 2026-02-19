package com.github.darksoulq.abyssallib.world.gui.layer;

import com.github.darksoulq.abyssallib.world.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.gui.GuiView;

import java.util.List;

/**
 * A container layer that manages a list of sub-layers, allowing only one to be active at a time.
 * <p>
 * This class provides navigation methods to cycle through the available layers,
 * effectively acting as a tab or page system for complex GUI layouts.
 *
 * @deprecated use {@link LayerStack}
 */
@Deprecated(since = "v2.0.0-mc1.21.11-dev.3", forRemoval = true)
public class ListedLayers extends LayerStack {

    /**
     * Constructs a new ListedLayers container with the provided sub-layers.
     *
     * @param layers the list of layers to be managed
     */
    public ListedLayers(List<GuiLayer> layers) {
        super(layers);
    }

    /**
     * Switches to the next layer in the list, wrapping around to the beginning if necessary.
     * <p>
     * Before switching, the current layer is cleaned up to ensure its elements
     * are removed from the view.
     *
     * @param view the active GUI view
     */
    public void next(GuiView view) {
        super.next(view);
    }

    /**
     * Switches to the previous layer in the list, wrapping around to the end if necessary.
     * <p>
     * Before switching, the current layer is cleaned up to ensure its elements
     * are removed from the view.
     *
     * @param view the active GUI view
     */
    public void prev(GuiView view) {
        super.previous(view);
    }

    /**
     * Gets the total number of layers managed by this container.
     *
     * @return the size of the layer list
     */
    public int getSize() {
        return super.size();
    }

    /**
     * Resets the render tracker, forcing the current layer to re-render on the next tick.
     */
    public void resetPage() {
        super.invalidate();
    }
}