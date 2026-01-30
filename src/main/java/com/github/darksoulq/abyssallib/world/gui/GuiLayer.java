package com.github.darksoulq.abyssallib.world.gui;

/**
 * Defines a visual layer that can be rendered over a GUI inventory.
 * <p>
 * Layers are used to provide background patterns, borders, or dynamic
 * multi-slot visuals that are not necessarily individual interactive elements.
 */
public interface GuiLayer {

    /**
     * Renders the layer's content onto the provided GUI view.
     *
     * @param view the active view instance to render onto
     */
    void renderTo(GuiView view);

    /**
     * Performs any necessary cleanup when the layer is no longer active.
     *
     * @param view the view instance being cleaned up
     */
    void cleanup(GuiView view);
}