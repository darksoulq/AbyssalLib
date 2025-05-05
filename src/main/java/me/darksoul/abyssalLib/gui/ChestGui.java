package me.darksoul.abyssalLib.gui;

import me.darksoul.abyssalLib.event.context.GuiClickContext;
import me.darksoul.abyssalLib.event.context.GuiCloseContext;
import me.darksoul.abyssalLib.event.context.GuiDragContext;
import me.darksoul.abyssalLib.resource.glyph.GuiTexture;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;

import java.util.List;

public abstract class ChestGui extends AbstractGui {

    public ChestGui(Player player, Component title, int rows) {
        super(player, title, typeByRows(rows));
    }
    public ChestGui(Player player, GuiTexture texture, int rows) {
        super(player, texture.getTitle(), typeByRows(rows));
    }

    public abstract void init(Player player);

    @Override
    public void _init() {
        init((Player) inventory().getPlayer());
        draw();
    }

    @Override
    public void _onClose(GuiCloseContext ctx) {
        onClose(ctx);
    }

    @Override
    public void draw() {
        if (slots.isEmpty()) {
            inventory().getTopInventory().clear();
            return;
        }
        for (Slot slot : slots) {
            inventory().getTopInventory().setItem(slot.index, slot.item());
        }
    }
    @Override
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
    @Override
    public void drawPartial() {
        if (slots.isEmpty()) {
            inventory().getTopInventory().clear();
            return;
        }
        List<Slot> changed = slots.stream()
                .filter(slot -> slot.item() != inventory().getItem(slot.index)).toList();
        for (Slot slot : changed) {
            inventory().getTopInventory().setItem(slot.index, slot.item());
        }
    }
    @Override
    public void handleClick(GuiClickContext ctx) {
        if (ctx.Gui().inventory() != inventory()) return;
        ctx.cancel();

        int index = ctx.slotIndex();
        slots.stream()
                .filter(s -> s.index == index)
                .findFirst()
                .ifPresent(slot -> slot.onClick(ctx));
    }
    @Override
    public void handleDrag(GuiDragContext ctx) {
        if (ctx.Gui().inventory() != inventory()) return;
        ctx.cancel();

        for (int index : ctx.draggedSlots()) {
            slots.stream()
                    .filter(s -> s.index == index)
                    .findFirst()
                    .ifPresent(slot -> slot.onDrag(ctx));
        }
    }

    public void onClose(GuiCloseContext ctx) {}

    private static MenuType typeByRows(int rows) {
        switch (rows) {
            case 1 -> {
                return MenuType.GENERIC_9X1;
            }
            case 2 -> {
                return MenuType.GENERIC_9X2;
            }
            case 3 -> {
                return MenuType.GENERIC_9X3;
            }
            case 4 -> {
                return MenuType.GENERIC_9X4;
            }
            case 5 -> {
                return MenuType.GENERIC_9X5;
            }
            case 6 -> {
                return MenuType.GENERIC_9X6;
            }
        }
        return MenuType.GENERIC_9X6;
    }
}
