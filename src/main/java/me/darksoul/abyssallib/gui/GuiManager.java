package me.darksoul.abyssallib.gui;

import me.darksoul.abyssallib.AbyssalLib;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the lifecycle of GUIs opened by players, including opening and ticking logic.
 */
public class GuiManager {
    public final Map<Player, AbstractGui> openGuis = new HashMap<>();

    /**
     * Constructs the GUI manager and starts the GUI ticking task. (called in {@link AbyssalLib}, access by {@link  AbyssalLib#GUI_MANAGER}
     */
    public GuiManager() {
        startTicking();
    }

    /**
     * Opens a new GUI for the player associated with the given {@link AbstractGui} instance.
     * <p>
     * If a GUI is already open for the player, it will be closed before the new one is opened.
     *
     * @param gui The GUI to open.
     */
    public void openGui(AbstractGui gui) {
        if (openGuis.containsKey((Player) gui.view().getPlayer())) {
            openGuis.get((Player) gui.view().getPlayer()).view().close();
            openGuis.remove((Player) gui.view().getPlayer());
        }
        openGuis.put((Player) gui.view().getPlayer(), gui);
        gui.view().open();
        gui.init((Player) gui.view().getPlayer());
    }

    /**
     * Starts a repeating task that calls {@link AbstractGui#tick()} on all open GUIs every server tick.
     * This is used for updating dynamic GUI elements.
     */
    public void startTicking() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, AbstractGui> entry : openGuis.entrySet()) {
                    AbstractGui gui = entry.getValue();
                    gui.tick();
                }
            }
        }.runTaskTimer(AbyssalLib.getInstance(), 1L, 1L);
    }
}
