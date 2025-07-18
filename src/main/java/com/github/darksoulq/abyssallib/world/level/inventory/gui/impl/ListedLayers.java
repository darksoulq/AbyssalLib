package com.github.darksoulq.abyssallib.world.level.inventory.gui.impl;

import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiView;

import java.util.ArrayList;
import java.util.List;

public class ListedLayers implements GuiLayer {
    private final List<GuiLayer> layers = new ArrayList<>();
    private int index = 0;

    public ListedLayers(List<GuiLayer> layers) {
        this.layers.addAll(layers);
    }

    public void next(GuiView view) {
        if (layers.isEmpty()) return;
        index = (index + 1) % layers.size();
        renderTo(view);
    }

    public void prev(GuiView view) {
        if (layers.isEmpty()) return;
        index = (index - 1 + layers.size()) % layers.size();
        renderTo(view);
    }

    @Override
    public void renderTo(GuiView view) {
        if (!layers.isEmpty()) {
            layers.get(index).renderTo(view);
        }
    }

    public int getIndex() {
        return index;
    }

    public int size() {
        return layers.size();
    }
}
