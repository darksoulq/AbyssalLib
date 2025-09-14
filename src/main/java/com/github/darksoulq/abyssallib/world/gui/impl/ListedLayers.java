package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.world.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.gui.GuiView;

import java.util.ArrayList;
import java.util.List;

public class ListedLayers implements GuiLayer {
    private final List<GuiLayer> layers = new ArrayList<>();
    private GuiLayer active = null;
    private int index = 0;

    public ListedLayers(List<GuiLayer> layers) {
        this.layers.addAll(layers);
    }

    public void next() {
        if (layers.isEmpty()) return;
        index = (index + 1) % layers.size();
    }

    public void prev() {
        if (layers.isEmpty()) return;
        index = (index - 1 + layers.size()) % layers.size();
    }

    @Override
    public void renderTo(GuiView view) {
        GuiLayer layer = layers.get(index);
        if (active != null && index != layers.indexOf(active)) cleanup(view);
        if (layer == null) return;
        if (layer != active) active = layer;
        layer.renderTo(view);
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
}
