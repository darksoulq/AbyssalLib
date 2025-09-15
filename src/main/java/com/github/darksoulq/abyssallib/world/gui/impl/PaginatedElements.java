package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.world.gui.*;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PaginatedElements implements GuiLayer {
    private final int[] slots;
    private final GuiView.Segment segment;
    private final List<GuiElement> source;
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
            cleanup(view);
        }
    }
    public void prev(GuiView view) {
        if (pageCount() <= 0) return;
        int newPage = (page - 1 + pageCount()) % pageCount();
        if (newPage != page) {
            page = newPage;
            cleanup(view);
        }
    }

    public int pageCount() {
        return (int) Math.ceil((double) filtered.size() / slots.length);
    }
    public int getPage() {
        return page;
    }

    @Override
    public void renderTo(GuiView view) {
        if (page == lastRenderedPage) return;
        Inventory inv = segment == GuiView.Segment.TOP ? view.getTop() : view.getBottom();

        Gui gui = view.getGui();
        int start = page * slots.length;

        for (int slot : slots) {
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

    @Override
    public void cleanup(GuiView view) {
        Inventory inv = segment == GuiView.Segment.TOP ? view.getTop() : view.getBottom();
        for (int slot : slots) {
            view.getGui().getElements().remove(new SlotPosition(segment, slot));
            inv.setItem(slot, null);
        }
    }
}
