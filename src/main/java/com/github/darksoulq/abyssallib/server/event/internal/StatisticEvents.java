package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistics;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.item.Item;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class StatisticEvents {

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Key blockId = Key.key(BlockInfo.resolve(e.getBlock()).getAsString());
        PlayerStatistics.of(e.getPlayer()).increment(Statistics.BLOCKS_MINED.get(blockId), 1);
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            String entityId;
            CustomEntity<?> custom = CustomEntity.resolve(e.getEntity());
            if (custom != null) {
                entityId = custom.getId().asString();
            } else {
                entityId = "minecraft:" + e.getEntity().getType().name().toLowerCase(Locale.ROOT);
            }
            PlayerStatistics.of(e.getEntity().getKiller()).increment(Statistics.ENTITIES_KILLED.get(Key.key(entityId)), 1);
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            ItemStack result = e.getRecipe().getResult();
            if (!result.isEmpty()) {
                Item item = Item.resolve(result);
                if (item == null) item = new Item(result);

                Key itemId = item.getId();
                PlayerStatistics.of(player).increment(Statistics.ITEMS_CRAFTED.get(itemId), result.getAmount());
            }
        }
    }
}