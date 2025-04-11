package me.darksoul.abyssalLib.gui;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.event.SubscribeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class GuiManager {
    private final Map<Player, AbyssalGui> openGuis = new HashMap<>();

    public GuiManager() {
        startTicking();
    }

    public void openGui(Player player, AbyssalGui gui) {
        openGuis.put(player, gui);
        player.openInventory(gui.inventory());
        gui.init();
    }

    public void closeGui(Player player) {
        openGuis.remove(player);
    }

    public void startTicking() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, AbyssalGui> entry : openGuis.entrySet()) {
                    AbyssalGui gui = entry.getValue();
                    if (gui.shouldTick()) {
                        gui.tick();
                    }
                }
            }
        }.runTaskTimer(AbyssalLib.getInstance(), 1L, 1L);
    }

    @SubscribeEvent
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        AbyssalGui gui = openGuis.get(player);
        if (gui == null || event.getClickedInventory() != gui.inventory()) return;
        gui.handleClick(new GuiClickContext(gui, event));
    }

    @SubscribeEvent
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        AbyssalGui gui = openGuis.get(player);
        if (gui == null || event.getInventory() != gui.inventory()) return;
        gui.handleDrag(new GuiDragContext(gui, event));
    }

    @SubscribeEvent
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        AbyssalGui gui = openGuis.remove(player);
        if (gui != null) {
            gui.onClose(new GuiCloseContext(gui, event));
        }
    }

    public AbyssalGui getOpenGui(Player player) {
        return openGuis.get(player);
    }
}
