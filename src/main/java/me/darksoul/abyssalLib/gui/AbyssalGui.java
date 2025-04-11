package me.darksoul.abyssalLib.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbyssalGui {
    protected final Player viewer;
    protected final Inventory inventory;
    protected final List<Slot> slots = new ArrayList<>();

    public AbyssalGui(Player viewer, String title, int rows) {
        this.viewer = viewer;
        this.inventory = Bukkit.createInventory(viewer, rows * 9, Component.text(title));
    }

    public void slot(Slot slot) {
        slots.add(slot);
    }

    public Inventory inventory() {
        return inventory;
    }

    public Player viewer() {
        return viewer;
    }

    public abstract void init();

    public void draw() {
        for (Slot slot : slots) {
            inventory.setItem(slot.index, slot.item());
        }
    }

    public boolean shouldTick() {
        return false; // override if this GUI should tick
    }

    public boolean dirtyDraw() {
        return true; // override if you only want to update when changes happen
    }

    public void tick() {
        for (Slot slot : slots) {
            slot.onTick(this);
        }
        if (dirtyDraw()) {
            drawPartial();
        } else {
            draw();
        }
    }

    public void drawPartial() {
        List<Slot> changed = slots.stream()
                .filter(slot -> slot.item() != inventory.getItem(slot.index)).toList();
        for (Slot slot : changed) {
            inventory.setItem(slot.index, slot.item());
        }
    }

    public void handleClick(GuiClickContext ctx) {
        if (ctx.Gui().inventory != inventory) return;
        ctx.cancel();

        int index = ctx.slotIndex();
        slots.stream()
                .filter(s -> s.index == index)
                .findFirst()
                .ifPresent(slot -> slot.onClick(ctx));
    }

    public void handleDrag(GuiDragContext ctx) {
        if (ctx.Gui().inventory != inventory) return;
        ctx.cancel();

        for (int index : ctx.draggedSlots()) {
            slots.stream()
                    .filter(s -> s.index == index)
                    .findFirst()
                    .ifPresent(slot -> slot.onDrag(ctx));
        }
    }

    public void onClose(GuiCloseContext ctx) {}
}
