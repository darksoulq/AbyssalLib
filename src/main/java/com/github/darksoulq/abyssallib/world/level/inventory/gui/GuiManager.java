package com.github.darksoulq.abyssallib.world.level.inventory.gui;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages opening, closing, and ticking of GUIs.
 * Supports shared GUIs across multiple players, and tracks ticking GUIs that require regular updates.
 */
public class GuiManager {

    /**
     * A map of currently open GUIs per player.
     */
    public final Map<Player, AbstractGui> openGuis = new HashMap<>();

    /**
     * A set of GUIs that require ticking (e.g., for animations or live updates).
     */
    private final Set<AbstractGui> tickingGuis = new HashSet<>();

    /**
     * Constructs the GuiManager and starts the ticking loop.
     */
    public GuiManager() {
        startTicking();
    }

    /**
     * Opens a GUI for a player. Closes any currently open GUI for that player.
     *
     * @param player the player for whom the GUI should be opened
     * @param gui    the GUI to open
     */
    public void openGui(Player player, AbstractGui gui) {
        closeGui(player);

        openGuis.put(player, gui);
        tickingGuis.add(gui);
        gui.open(player);
    }

    /**
     * Closes the GUI currently open for the given player.
     * Removes the GUI from the ticking set if it has no remaining viewers.
     *
     * @param player the player whose GUI should be closed
     */
    public void closeGui(Player player) {
        AbstractGui gui = openGuis.remove(player);
        if (gui != null) {
            gui.close(player);
            if (gui.viewers().isEmpty()) {
                tickingGuis.remove(gui);
            }
        }
    }

    /**
     * Starts the ticking loop that calls {@code tick()} on all ticking GUIs every tick (1L interval).
     */
    public void startTicking() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (AbstractGui gui : tickingGuis) {
                    gui.tick();
                }
            }
        }.runTaskTimer(AbyssalLib.getInstance(), 1L, 1L);
    }
}
