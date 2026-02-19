package com.github.darksoulq.abyssallib.world.gui.layer;

import com.github.darksoulq.abyssallib.world.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.gui.GuiView;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite GUI layer that manages a collection of sub-layers in a stack.
 * <p>
 * Only one layer from the stack is rendered at any given time. This class
 * facilitates navigation through these layers, handling the cleanup of
 * the previous layer before rendering the next one.
 */
public class LayerStack implements GuiLayer {

    /** The internal list of managed GUI layers. */
    protected final List<GuiLayer> layers = new ArrayList<>();

    /** The index of the currently active layer. */
    protected int index = 0;

    /** Tracks the last rendered index to optimize rendering updates. */
    protected int lastRenderedIndex = -1;

    /**
     * Constructs a new LayerStack with the provided layers.
     *
     * @param layers the list of layers to include in the stack
     */
    public LayerStack(List<GuiLayer> layers) {
        this.layers.addAll(layers);
    }

    /**
     * Switches to the next layer in the stack, wrapping back to the start if necessary.
     *
     * @param view the active GUI view context
     */
    public void next(GuiView view) {
        if (layers.isEmpty()) return;
        layers.get(index).cleanup(view);
        index = (index + 1) % layers.size();
    }

    /**
     * Switches to the previous layer in the stack, wrapping to the end if necessary.
     *
     * @param view the active GUI view context
     */
    public void previous(GuiView view) {
        if (layers.isEmpty()) return;
        layers.get(index).cleanup(view);
        index = (index - 1 + layers.size()) % layers.size();
    }

    /**
     * Jumps directly to a specific layer index in the stack.
     *
     * @param view  the active GUI view context
     * @param index the target index
     */
    public void setIndex(GuiView view, int index) {
        if (index < 0 || index >= layers.size()) return;
        if (this.index != index) {
            layers.get(this.index).cleanup(view);
            this.index = index;
        }
    }

    /**
     * Renders the currently active layer to the GUI view.
     * <p>
     * Rendering only occurs if the active index has changed since the last update.
     *
     * @param view the view instance to render into
     */
    @Override
    public void renderTo(GuiView view) {
        if (layers.isEmpty()) return;
        if (index == lastRenderedIndex) return;

        GuiLayer layer = layers.get(index);
        layer.renderTo(view);
        lastRenderedIndex = index;
    }

    /**
     * Cleans up the currently active layer.
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
     * Gets the index of the current active layer.
     *
     * @return the current index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the total number of layers in the stack.
     *
     * @return the layer count
     */
    public int size() {
        return layers.size();
    }

    /**
     * Forces a re-render on the next tick by invalidating the render cache.
     */
    public void invalidate() {
        lastRenderedIndex = -1;
    }
}