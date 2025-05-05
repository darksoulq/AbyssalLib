package me.darksoul.abyssalLib.gui;

import me.darksoul.abyssalLib.event.context.GuiClickContext;
import me.darksoul.abyssalLib.event.context.GuiCloseContext;
import me.darksoul.abyssalLib.event.context.GuiDragContext;
import me.darksoul.abyssalLib.gui.slot.StaticSlot;
import me.darksoul.abyssalLib.resource.glyph.GuiTexture;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;

import java.util.*;

public abstract class SearchGui extends AbstractGui {
    private static final Map<Player, ItemStack[]> backupMap = new HashMap<>();

    private Inventory bottomInventory;
    private String text;
    private ItemStack invisItem = new ItemStack(Material.PAPER);
    private final StaticSlot inputSlot;

    public SearchGui(Player player, GuiTexture texture) {
        super(player, texture.getTitle(), MenuType.ANVIL);

        invisItem.editMeta((itemMeta -> {
            itemMeta.itemName(Component.text().build());
        }));
        inputSlot = new StaticSlot(0, invisItem);
    }

    public SearchGui(Player player, Component title) {
        super(player, title, MenuType.ANVIL);

        invisItem.editMeta((itemMeta -> {
            itemMeta.itemName(Component.text().build());
        }));
        inputSlot = new StaticSlot(0, invisItem);
    }

    public abstract void init(Player player);

    public String text() {
        return text;
    }

    @Override
    public void _init() {
        text = "";
        bottomInventory = inventory().getBottomInventory();

        ItemStack[] originalContents = bottomInventory.getContents();
        backupMap.put((Player) inventory().getPlayer(), Arrays.copyOf(originalContents, originalContents.length));

        inventory().getBottomInventory().clear();
        init((Player) inventory().getPlayer());
    }

    @Override
    public void draw() {
        if (allowInput()) {
            inventory().getTopInventory().setItem(inputSlot.index, inputSlot.item());
        }
        if (slots.isEmpty()) {
            bottomInventory.clear();
            return;
        }
        for (Slot slot : slots) {
            bottomInventory.setItem(slot.index, slot.item());
        }
    }

    @Override
    public void drawPartial() {
        if (allowInput()) {
            inventory().getTopInventory().setItem(inputSlot.index, inputSlot.item());
        }
        if (slots.isEmpty()) {
            bottomInventory.clear();
            return;
        }

        List<Slot> changed = slots.stream()
                .filter(slot -> !Objects.equals(slot.item(), inventory().getItem(slot.index)))
                .toList();

        for (Slot slot : changed) {
            bottomInventory.setItem(slot.index, slot.item());
        }
    }

    public void onTick() {
        if (dirtyDraw()) {
            drawPartial();
        } else {
            draw();
        }
    }

    @Override
    public void tick() {
        for (Slot slot : slots) {
            slot.onTick(this);
        }

        onTick();
        text = ((AnvilView) inventory()).getRenameText();
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

    public boolean allowInput() {
        return true;
    }

    public void onClose(GuiCloseContext ctx) {}
    @Override
    public void _onClose(GuiCloseContext ctx) {
        restoreBottomMenu();
        onClose(ctx);
    }

    public void restoreBottomMenu() {
        Player player = (Player) inventory().getPlayer();

        if (backupMap.containsKey(player)) {
            player.getInventory().setContents(backupMap.get(player));
            backupMap.remove(player);
        }
    }
}