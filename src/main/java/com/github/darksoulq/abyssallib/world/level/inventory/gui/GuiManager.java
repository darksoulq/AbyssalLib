package com.github.darksoulq.abyssallib.world.level.inventory.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class GuiManager {
    public static final Map<InventoryView, GuiView> openViews = new HashMap<>();

    public static void open(HumanEntity player, Gui gui) {
        InventoryView view = gui.getMenuType().create(player, gui.getTitle());
        view.open();
        GuiView guiView = new GuiView(gui, view);
        gui.getOnOpen().accept(guiView);
        openViews.put(view, guiView);
    }

    public static void close(HumanEntity player) {
        InventoryView view = player.getOpenInventory();
        GuiView guiView = openViews.remove(view);
        if (guiView != null) {
            guiView.close(player);
        }
        view.close();
    }

    public static void init(Plugin pl) {
        new BukkitRunnable() {
            @Override
            public void run() {
                openViews.values().forEach(GuiView::tick);
            }
        }.runTaskTimer(pl, 0, 1);
    }
}
