package com.github.darksoulq.abyssallib.world.item.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.github.darksoulq.abyssallib.server.scheduler.ScheduledTask;
import com.github.darksoulq.abyssallib.world.item.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemTicker {
    private static final Map<Player, List<Item>> items = new ConcurrentHashMap<>();
    private static ScheduledTask task = null;

    public static void start() {
        if (task != null) return;
        task = AbyssalLib.SCHEDULER.schedule(() -> {
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
        }).repeatEvery(5L, Clock.TICKS);
    }

    public static void update(Player player) {
        AbyssalLib.SCHEDULER.schedule(() -> {
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
        }).async().once();
    }

    public static void remove(Player player) {
        items.remove(player);
    }
}