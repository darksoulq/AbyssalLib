package com.github.darksoulq.abyssallib.world.level.inventory.gui.impl;

import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PaginatedLayer implements GuiLayer {
    private final int[] slots;
    private List<GuiElement> source;
    private List<GuiElement> filtered;
    private Predicate<GuiElement> filter = el -> true;
    private int page = 0;

    public PaginatedLayer(List<GuiElement> source, int[] slots) {
        this.source = new ArrayList<>(source);
        this.slots = slots;
        this.filtered = source.stream().filter(filter).toList();
    }

    public void setFilter(Predicate<GuiElement> filter) {
        this.filter = filter;
        this.filtered = source.stream().filter(filter).toList();
        this.page = 0;
    }

    public void next(GuiView view) {
        page = (page + 1) % Math.max(1, pageCount());
        renderTo(view);
    }

    public void prev(GuiView view) {
        page = (page - 1 + pageCount()) % Math.max(1, pageCount());
        renderTo(view);
    }

    public int pageCount() {
        return (int) Math.ceil((double) filtered.size() / slots.length);
    }

    @Override
    public void renderTo(GuiView view) {
        int start = page * slots.length;
        for (int i = 0; i < slots.length; i++) {
            int global = start + i;
            ItemStack item = null;
            if (global < filtered.size()) {
                GuiElement el = filtered.get(global);
                item = el.render(view, slots[i]);
            }
            view.getTop().setItem(slots[i], item);
        }
    }
}
