package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.world.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.gui.GuiView;

import java.util.ArrayList;
import java.util.List;

public class ListedLayers implements GuiLayer {
    private final List<GuiLayer> layers = new ArrayList<>();
    private int index = 0;
    private int lastRenderedPage = -1;

    public ListedLayers(List<GuiLayer> layers) {
        this.layers.addAll(layers);
    }

    public void next(GuiView view) {
        if (layers.isEmpty()) return;
        GuiLayer layer = layers.get(index);
        index = (index + 1) % layers.size();
        layer.cleanup(view);
    }

    public void prev(GuiView view) {
        if (layers.isEmpty()) return;
        GuiLayer layer = layers.get(index);
        index = (index - 1 + layers.size()) % layers.size();
        layer.cleanup(view);
    }

    @Override
    public void renderTo(GuiView view) {
        if (index == lastRenderedPage) return;
        GuiLayer layer = layers.get(index);
        if (layer == null) return;
        layer.renderTo(view);
        lastRenderedPage = index;
    }

    @Override
    public void cleanup(GuiView view) {
        if (!layers.isEmpty()) {
            layers.get(index).cleanup(view);
        }
    }

    public int getIndex() {
        return index;
    }
    public int getSize() {
        return layers.size();
    }
    public void resetPage() {
        lastRenderedPage = -1;
    }
}
