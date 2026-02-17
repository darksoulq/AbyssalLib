package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.world.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.gui.GuiView;

import java.util.ArrayList;
import java.util.List;

public class LayerStack implements GuiLayer {

    protected final List<GuiLayer> layers = new ArrayList<>();
    protected int index = 0;
    protected int lastRenderedIndex = -1;

    public LayerStack(List<GuiLayer> layers) {
        this.layers.addAll(layers);
    }

    public void next(GuiView view) {
        if (layers.isEmpty()) return;
        layers.get(index).cleanup(view);
        index = (index + 1) % layers.size();
    }

    public void previous(GuiView view) {
        if (layers.isEmpty()) return;
        layers.get(index).cleanup(view);
        index = (index - 1 + layers.size()) % layers.size();
    }

    public void setIndex(GuiView view, int index) {
        if (index < 0 || index >= layers.size()) return;
        if (this.index != index) {
            layers.get(this.index).cleanup(view);
            this.index = index;
        }
    }

    @Override
    public void renderTo(GuiView view) {
        if (layers.isEmpty()) return;
        if (index == lastRenderedIndex) return;
        
        GuiLayer layer = layers.get(index);
        layer.renderTo(view);
        lastRenderedIndex = index;
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

    public int size() {
        return layers.size();
    }
    
    public void invalidate() {
        lastRenderedIndex = -1;
    }
}