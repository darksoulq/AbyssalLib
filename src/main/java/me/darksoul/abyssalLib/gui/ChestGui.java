package me.darksoul.abyssalLib.gui;

import me.darksoul.abyssalLib.event.context.GuiClickContext;
import me.darksoul.abyssalLib.event.context.GuiCloseContext;
import me.darksoul.abyssalLib.event.context.GuiDragContext;
import me.darksoul.abyssalLib.resource.glyph.GuiTexture;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class ChestGui extends AbstractGui {

    public ChestGui(Component title, int rows) {
        super(title, rows * 9);
    }
    public ChestGui(GuiTexture texture, int rows) {
        super(texture.getTitle(), rows * 9);
    }

    public abstract void init(Player player);

    @Override
    public void _init(Player player) {
        init(player);
        draw();
    }

    @Override
    public void _onClose(GuiCloseContext ctx) {
        onClose(ctx);
    }

    @Override
    public void draw() {
        if (slots.isEmpty()) {
            inventory().clear();
            return;
        }
        for (Slot slot : slots) {
            inventory().setItem(slot.index, slot.item());
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
            inventory().clear();
            return;
        }
        List<Slot> changed = slots.stream()
                .filter(slot -> slot.item() != inventory().getItem(slot.index)).toList();
        for (Slot slot : changed) {
            inventory().setItem(slot.index, slot.item());
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
}
