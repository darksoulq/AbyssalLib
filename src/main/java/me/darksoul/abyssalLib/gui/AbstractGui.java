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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractGui implements InventoryHolder {

    private final Inventory inventory;
    protected final List<Slot> slots = new ArrayList<>();

    public AbstractGui(Component title, InventoryType type) {
        this.inventory = AbyssalLib.getInstance().getServer().createInventory(this, type, title);
    }
    public AbstractGui(Component title, int rows) {
        this.inventory = AbyssalLib.getInstance().getServer().createInventory(this, rows, title);
    }

    public void slot(Slot slot) {
        slots.add(slot);
    }

    public Inventory inventory() {
        return inventory;
    }

    public abstract void _init(Player player);

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

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
