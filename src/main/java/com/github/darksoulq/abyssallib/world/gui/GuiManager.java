package com.github.darksoulq.abyssallib.world.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Central manager for handling the lifecycle and updates of custom GUIs.
 * <p>
 * This class facilitates opening and closing menus, tracking active views,
 * and running the global tick task that drives GUI animations and logic.
 */
public class GuiManager {

    /** A map of active Bukkit InventoryViews to their corresponding AbyssalLib GuiViews. */
    public static final Map<InventoryView, GuiView> openViews = new HashMap<>();

    /**
     * Opens a custom GUI for a player.
     * <p>
     * This method validates the player's state (e.g., not sleeping) before
     * creating the inventory and registering the active view.
     *
     * @param player the player for whom to open the GUI
     * @param gui    the GUI template to display
     */
    public static void open(HumanEntity player, Gui gui) {
        if (player.isSleeping() || player.getPortalCooldown() > 0) return;
        InventoryView view = gui.getMenuType().create(player, gui.getTitle());
        view.open();
        GuiView guiView = new GuiView(gui, view);
        gui.getOnOpen().accept(guiView);
        openViews.put(view, guiView);
    }

    /**
     * Closes the currently open GUI for a player and unregisters its view.
     *
     * @param player the player whose GUI should be closed
     */
    public static void close(HumanEntity player) {
        InventoryView view = player.getOpenInventory();
        GuiView guiView = openViews.remove(view);
        if (guiView != null) {
            guiView.close(player);
        }
        view.close();
    }

    /**
     * Initializes the GUI system by starting the per-tick update task.
     *
     * @param pl the plugin instance responsible for the task
     */
    public static void init(Plugin pl) {
        new BukkitRunnable() {
            /**
             * Iterates through all active GUI views and triggers their tick logic.
             */
            @Override
            public void run() {
                openViews.values().forEach(GuiView::tick);
            }
        }.runTaskTimer(pl, 0, 1);
    }
}