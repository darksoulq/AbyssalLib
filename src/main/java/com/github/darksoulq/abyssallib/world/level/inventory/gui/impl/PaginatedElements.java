package com.github.darksoulq.abyssallib.world.level.inventory.gui.impl;

import com.github.darksoulq.abyssallib.world.level.inventory.gui.*;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PaginatedElements implements GuiLayer {
    private final int[] slots;
    private final GuiView.Segment segment;
    private List<GuiElement> source;
    private List<GuiElement> filtered;
    private Predicate<GuiElement> filter = el -> true;
    private int page = 0;
    private int lastRenderedPage = -1;

    public PaginatedElements(List<GuiElement> source, int[] slots, GuiView.Segment segment) {
        this.source = new ArrayList<>(source);
        this.slots = slots;
        this.segment = segment;
        this.filtered = source.stream().filter(filter).toList();
    }

    public void setFilter(Predicate<GuiElement> filter) {
        this.filter = filter;
        this.filtered = source.stream().filter(filter).toList();
        this.page = 0;
        this.lastRenderedPage = -1;
    }

    public void next(GuiView view) {
        if (pageCount() <= 0) return;
        int newPage = (page + 1) % pageCount();
        if (newPage != page) {
            page = newPage;
            renderTo(view);
        }
    }

    public void prev(GuiView view) {
        if (pageCount() <= 0) return;
        int newPage = (page - 1 + pageCount()) % pageCount();
        if (newPage != page) {
            page = newPage;
            renderTo(view);
        }
    }

    public int pageCount() {
        return (int) Math.ceil((double) filtered.size() / slots.length);
    }

    @Override
    public void renderTo(GuiView view) {
        if (page == lastRenderedPage) return;

        Gui gui = view.getGui();
        Inventory inv = (segment == GuiView.Segment.TOP ? view.getTop() : view.getBottom());

        int start = page * slots.length;

        for (int slot : slots) {
            SlotPosition pos = new SlotPosition(segment, slot);
            gui.getElements().remove(pos);
            inv.setItem(slot, null);
        }

        for (int i = 0; i < slots.length; i++) {
            int global = start + i;
            if (global >= filtered.size()) break;

            int slot = slots[i];
            GuiElement el = filtered.get(global);
            SlotPosition pos = new SlotPosition(segment, slot);

            gui.getElements().put(pos, el);
        }

        lastRenderedPage = page;
    }
}
