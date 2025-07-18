package com.github.darksoulq.abyssallib.world.level.inventory.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

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
        gui.getLayers().forEach(layer -> layer.renderTo(this));
        gui.getTickers().forEach(t -> t.accept(this));
    }

    public void close(HumanEntity player) {
        player.closeInventory();
        gui.getOnClose().accept(this);
    }

    public GuiElement getElementAt(Segment segment, int slot) {
        return gui.getElements().get(new SlotPosition(segment, slot));
    }
}
