package com.github.darksoulq.abyssallib.world.item.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.util.TaskUtil;
import com.github.darksoulq.abyssallib.world.item.Item;
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
        task = TaskUtil.repeatingTask(AbyssalLib.getInstance(), 0, 1, () -> {
            for (Player player : items.keySet()) {
                List<Item> item = items.get(player);
                if (item == null || item.isEmpty()) continue;
                for (Item i : item) {
                    i.onInventoryTick(player);
                }
            }
        });
    }

    public static void update(Player player) {
        List<Item> items = new ArrayList<>();
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null) continue;
            Item item = Item.resolve(stack);
            if (item != null) items.add(item);
        }
        if (items.isEmpty()) {
            remove(player);
            return;
        }
        ItemTicker.items.put(player, items);
    }

    public static void remove(Player player) {
        items.remove(player);
    }
}
