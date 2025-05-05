package me.darksoul.abyssalLib.gui;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.event.SubscribeEvent;
import me.darksoul.abyssalLib.event.context.GuiClickContext;
import me.darksoul.abyssalLib.event.context.GuiCloseContext;
import me.darksoul.abyssalLib.event.context.GuiDragContext;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class GuiManager {
    private final Map<Player, AbstractGui> openGuis = new HashMap<>();

    public GuiManager() {
        startTicking();
    }

    public void openGui(AbstractGui gui) {
        openGuis.put((Player) gui.inventory().getPlayer(), gui);
        gui.inventory().open();
        gui._init();
    }

    public void closeGui(Player player) {
        openGuis.remove(player);
    }

    public void startTicking() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, AbstractGui> entry : openGuis.entrySet()) {
                    AbstractGui gui = entry.getValue();
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
        AbstractGui gui = openGuis.get(player);
        if (gui == null || event.getClickedInventory() != gui.inventory().getTopInventory() && event.getClickedInventory() != gui.inventory().getBottomInventory()) return;
        gui.handleClick(new GuiClickContext(gui, event));
    }

    @SubscribeEvent
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        AbstractGui gui = openGuis.get(player);
        if (gui == null || event.getInventory() != gui.inventory() && event.getInventory() != gui.inventory().getBottomInventory()) return;
        gui.handleDrag(new GuiDragContext(gui, event));
    }

    @SubscribeEvent
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        AbstractGui gui = openGuis.remove(player);
        if (gui != null) {
            gui._onClose(new GuiCloseContext(gui, event));
        }
    }

    public AbstractGui getOpenGui(Player player) {
        return openGuis.get(player);
    }
}
