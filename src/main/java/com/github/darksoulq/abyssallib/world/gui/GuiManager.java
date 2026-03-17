package com.github.darksoulq.abyssallib.world.gui;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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
    public static final Map<InventoryView, GuiView> OPEN_VIEWS = new HashMap<>();
    /**
     * A map of active BukkitTasks to their corresponding GUiViews.
     */
    public static final Map<GuiView, BukkitTask> TICK_VIEWS = new HashMap<>();

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
        guiView.render();
        gui.getOnOpen().accept(guiView);
        OPEN_VIEWS.put(view, guiView);
        if (gui.getTickInterval() > 0) TICK_VIEWS.put(guiView, startGuiTick(guiView, gui.getTickInterval()));
    }

    /**
     * Closes the currently open GUI for a player and unregisters its view.
     *
     * @param player the player whose GUI should be closed
     */
    public static void close(HumanEntity player) {
        InventoryView view = player.getOpenInventory();
        GuiView guiView = OPEN_VIEWS.remove(view);
        if (guiView != null) {
            BukkitTask task = TICK_VIEWS.remove(guiView);
            if (task != null) task.cancel();
            guiView.close(player);
        }
        view.close();
    }

    /**
     * Removed the GUiView from both Maps so that users may open a new gui over old gui.
     *
     * @param view THe view to remove.
     */
    public static void remove(GuiView view) {
        OPEN_VIEWS.remove(view.getInventoryView());
        TICK_VIEWS.remove(view);
    }

    /**
     * Starts a ticking tasks for a GuiView.
     *
     * @param view THe guiView to tick.
     * @param interval The interval between ticks.
     * @return The BukkitTask that can be cancelled whenever needed.
     */
    private static BukkitTask startGuiTick(GuiView view, int interval) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                view.render();
            }
        }.runTaskTimer(AbyssalLib.getInstance(), 0, interval);
    }
}