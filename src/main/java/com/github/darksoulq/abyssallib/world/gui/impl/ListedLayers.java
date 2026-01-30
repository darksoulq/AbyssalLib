package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.world.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.gui.GuiView;

import java.util.ArrayList;
import java.util.List;

/**
 * A container layer that manages a list of sub-layers, allowing only one to be active at a time.
 * <p>
 * This class provides navigation methods to cycle through the available layers,
 * effectively acting as a tab or page system for complex GUI layouts.
 */
public class ListedLayers implements GuiLayer {
    /** The collection of sub-layers managed by this container. */
    private final List<GuiLayer> layers = new ArrayList<>();

    /** The index of the currently active layer. */
    private int index = 0;

    /** Tracks the last rendered index to prevent unnecessary re-rendering logic. */
    private int lastRenderedPage = -1;

    /**
     * Constructs a new ListedLayers container with the provided sub-layers.
     *
     * @param layers the list of layers to be managed
     */
    public ListedLayers(List<GuiLayer> layers) {
        this.layers.addAll(layers);
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
        if (layers.isEmpty()) return;
        GuiLayer layer = layers.get(index);
        index = (index + 1) % layers.size();
        layer.cleanup(view);
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
        if (layers.isEmpty()) return;
        GuiLayer layer = layers.get(index);
        index = (index - 1 + layers.size()) % layers.size();
        layer.cleanup(view);
    }

    /**
     * Renders the currently active sub-layer to the specified GUI view.
     * <p>
     * If the layer index has changed since the last render, the new layer's
     * {@link GuiLayer#renderTo(GuiView)} method is invoked.
     *
     * @param view the active view to render into
     */
    @Override
    public void renderTo(GuiView view) {
        if (index == lastRenderedPage) return;
        GuiLayer layer = layers.get(index);
        if (layer == null) return;
        layer.renderTo(view);
        lastRenderedPage = index;
    }

    /**
     * Cleans up the currently active sub-layer.
     *
     * @param view the view instance being cleaned up
     */
    @Override
    public void cleanup(GuiView view) {
        if (!layers.isEmpty()) {
            layers.get(index).cleanup(view);
        }
    }

    /**
     * Gets the index of the currently active layer.
     *
     * @return the current layer index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the total number of layers managed by this container.
     *
     * @return the size of the layer list
     */
    public int getSize() {
        return layers.size();
    }

    /**
     * Resets the render tracker, forcing the current layer to re-render on the next tick.
     */
    public void resetPage() {
        lastRenderedPage = -1;
    }
}