package com.github.darksoulq.abyssallib.world.level.inventory.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class GuiManager {
    public static final Map<InventoryView, GuiView> openViews = new HashMap<>();

    public static void open(HumanEntity player, Gui gui) {
        InventoryView view = gui.getMenuType().create(player, gui.getTitle());
        GuiView guiView = new GuiView(gui, view);
        openViews.put(view, guiView);
        gui.getOnOpen().accept(guiView);
    }

    public static void close(HumanEntity player) {
        InventoryView view = player.getOpenInventory();
        GuiView guiView = openViews.remove(view);
        if (guiView != null) {
            guiView.close(player);
        }
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
