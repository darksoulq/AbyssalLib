package com.github.darksoulq.abyssallib.world.item.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.world.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemTicker {
    private static final Map<Player, List<Item>> items = new ConcurrentHashMap<>();
    private static BukkitTask task = null;

    public static void start() {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskTimer(AbyssalLib.getInstance(), () -> {
            for (Map.Entry<Player, List<Item>> entry : items.entrySet()) {
                Player player = entry.getKey();
                if (player == null || !player.isOnline()) {
                    continue;
                }
                List<Item> itemList = entry.getValue();
                if (itemList == null || itemList.isEmpty()) continue;
                for (Item i : itemList) {
                    i.onInventoryTick(player);
                }
            }
        }, 0L, 5L);
    }

    public static void update(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(AbyssalLib.getInstance(), () -> {
            List<Item> playerItems = new ArrayList<>();
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack == null) continue;
                Item item = Item.resolve(stack);
                if (item != null) playerItems.add(item);
            }
            if (playerItems.isEmpty()) {
                remove(player);
            } else {
                items.put(player, playerItems);
            }
        });
    }

    public static void remove(Player player) {
        items.remove(player);
    }
}