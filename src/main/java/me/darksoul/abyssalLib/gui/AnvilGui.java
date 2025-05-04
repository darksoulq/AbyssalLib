package me.darksoul.abyssalLib.gui;

import me.darksoul.abyssalLib.event.context.GuiCloseContext;
import me.darksoul.abyssalLib.resource.glyph.GuiTexture;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AnvilGui extends AbstractGui {
    private static Map<Player, ItemStack[]> backupMap = new HashMap<>();

    private Inventory bottomInventory;
    private String text;
    private AnvilView view;

    public AnvilGui(GuiTexture texture) {
        super(texture.getTitle(), InventoryType.ANVIL);
    }
    public AnvilGui(Component title) {
        super(title, InventoryType.ANVIL);
    }

    public abstract void init(Player player);
    public String text() {
        return text;
    }

    @Override
    public void _init(Player player) {
        text = "";
        view = (AnvilView) player.getOpenInventory();
        bottomInventory = view.getBottomInventory();
        backupMap.put(player, bottomInventory.getContents());
        player.getInventory().setContents(new ItemStack[] {});
        init(player);
    }

    @Override
    public void draw() {
        for (Slot slot : slots) {
            bottomInventory.setItem(slot.index, slot.item());
        }
    }

    @Override
    public void drawPartial() {
        if (slots.isEmpty()) {
            bottomInventory.clear();
            return;
        }
        List<Slot> changed = slots.stream()
                .filter(slot -> slot.item() != inventory().getItem(slot.index)).toList();
        for (Slot slot : changed) {
            bottomInventory.setItem(slot.index, slot.item());
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
        text = view.getRenameText();
    }

    public abstract void onClose(GuiCloseContext ctx);
    @Override
    public void _onClose(GuiCloseContext ctx) {
        ctx.player().getInventory().setContents(new ItemStack[] {});
        ctx.player().getInventory().setContents(backupMap.get(ctx.player()));
        onClose(ctx);
    }

    @Override
    public Inventory inventory() {
        return bottomInventory;
    }
}
