package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.world.gui.*;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class PagedLayer<T> implements GuiLayer {

    protected final List<T> source;
    protected final BiFunction<T, Integer, GuiElement> mapper;
    protected final int[] slots;
    protected final GuiView.Segment segment;

    protected List<T> filtered;
    protected Predicate<T> filter = t -> true;

    protected int page = 0;
    protected int lastRenderedPage = -1;

    public PagedLayer(List<T> source, int[] slots, GuiView.Segment segment, BiFunction<T, Integer, GuiElement> mapper) {
        this.source = new ArrayList<>(source);
        this.slots = slots;
        this.segment = segment;
        this.mapper = mapper;
        this.filtered = new ArrayList<>(this.source);
    }

    public PagedLayer(List<T> source, int[] slots, GuiView.Segment segment) {
        this(source, slots, segment, (t, i) -> (GuiElement) t);
    }

    public void setFilter(Predicate<T> filter) {
        this.filter = filter;
        this.filtered = source.stream().filter(filter).toList();
        this.page = 0;
        this.lastRenderedPage = -1;
    }

    public void next(GuiView view) {
        if (getPageCount() <= 0) return;
        int next = (page + 1) % getPageCount();
        if (next != page) {
            page = next;
            cleanup(view);
        }
    }

    public void previous(GuiView view) {
        if (getPageCount() <= 0) return;
        int prev = (page - 1 + getPageCount()) % getPageCount();
        if (prev != page) {
            page = prev;
            cleanup(view);
        }
    }

    @Override
    public void renderTo(GuiView view) {
        if (page == lastRenderedPage) return;
        cleanup(view);

        Gui gui = view.getGui();
        int start = page * slots.length;

        for (int i = 0; i < slots.length; i++) {
            int globalIndex = start + i;
            if (globalIndex >= filtered.size()) break;

            int slotIndex = slots[i];
            T data = filtered.get(globalIndex);
            GuiElement element = mapper.apply(data, globalIndex);

            gui.getElements().put(new SlotPosition(segment, slotIndex), element);
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

    public int getPageCount() {
        return (int) Math.ceil((double) filtered.size() / slots.length);
    }
    public int getPage() {
        return page;
    }
    public void invalidate() {
        lastRenderedPage = -1;
    }

    public static <T> PagedLayer<T> of(List<T> source, int[] slots, GuiView.Segment segment, BiFunction<T, Integer, GuiElement> mapper) {
        return new PagedLayer<>(source, slots, segment, mapper);
    }
    public static PagedLayer<GuiElement> of(List<GuiElement> elements, int[] slots, GuiView.Segment segment) {
        return new PagedLayer<>(elements, slots, segment);
    }
}