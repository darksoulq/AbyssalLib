package me.darksoul.abyssalLib.gui;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.event.context.GuiClickContext;
import me.darksoul.abyssalLib.event.context.GuiCloseContext;
import me.darksoul.abyssalLib.event.context.GuiDragContext;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractGui {

    private final InventoryView inventory;
    protected final List<Slot> slots = new ArrayList<>();

    public AbstractGui(Player player, Component title, MenuType type) {
        this.inventory = type.create(player, title);
    }

    public void slot(Slot slot) {
        slots.add(slot);
    }

    public InventoryView inventory() {
        return inventory;
    }

    public abstract void _init();
    public void draw() {
    }

    public boolean shouldTick() {
        return true;
    }
    public boolean dirtyDraw() {
        return true;
    }

    public void tick() {
    }
    public void drawPartial() {
    }
    public void handleClick(GuiClickContext ctx) {
    }
    public void handleDrag(GuiDragContext ctx) {
    }
    public void _onClose(GuiCloseContext ctx) {}

    public Map<Integer, Slot> slotMap() {
        return slots.stream()
                .collect(Collectors.toMap(Slot::index, Function.identity()));
    }
}
