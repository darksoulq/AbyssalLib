package com.github.darksoulq.abyssallib.world.level.inventory.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class GuiView {
    public enum Segment { TOP, BOTTOM }

    private final Gui gui;
    private final InventoryView view;

    public GuiView(Gui gui, InventoryView view) {
        this.gui = gui;
        this.view = view;
    }

    public Gui getGui() {
        return gui;
    }

    public InventoryView getInventoryView() {
        return view;
    }

    public Inventory getTop() {
        return view.getTopInventory();
    }

    public Inventory getBottom() {
        return view.getBottomInventory();
    }

    public void tick() {
        Inventory top = getTop();
        Inventory bot = getBottom();
        gui.getLayers().forEach(layer -> layer.renderTo(this));
        gui.getTickers().forEach(t -> t.accept(this));
        gui.getElements().forEach((s, e) -> {
            ItemStack item = e.render(this, s.index());
            if (item != null) {
                if (s.segment() == Segment.TOP) {
                    if (top.getItem(s.index()) != item) {
                        top.setItem(s.index(), item);
                    }
                } else {
                    if (bot.getItem(s.index()) != item) {
                        bot.setItem(s.index(), item);
                    }
                }
            }
        });
    }

    public void close(HumanEntity player) {
        player.closeInventory();
        gui.getOnClose().accept(this);
    }

    public GuiElement getElementAt(Segment segment, int slot) {
        return gui.getElements().get(new SlotPosition(segment, slot));
    }
}
