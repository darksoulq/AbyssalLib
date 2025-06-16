package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.player.PlayerEnterWaterEvent;
import com.github.darksoulq.abyssallib.server.event.custom.player.PlayerExitWaterEvent;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class PlayerEvents {

    @SubscribeEvent
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Block fromBlock = event.getFrom().getBlock();
        Block toBlock = event.getTo().getBlock();

        boolean wasInWater = isWaterBlock(fromBlock);
        boolean isInWater = isWaterBlock(toBlock);

        Vector velocity = player.getVelocity();

        if (!wasInWater && isInWater && velocity.getY() < -0.1) {
            PlayerEnterWaterEvent fallEvent = new PlayerEnterWaterEvent(player, toBlock.getLocation(), velocity);
            Bukkit.getServer().getPluginManager().callEvent(fallEvent);
        }

        if (wasInWater && !isInWater) {
            PlayerExitWaterEvent exitEvent = new PlayerExitWaterEvent(player, toBlock.getLocation());
            Bukkit.getServer().getPluginManager().callEvent(exitEvent);
        }
    }

    @SubscribeEvent
    public void onPick(PlayerPickBlockEvent event) {
        if (com.github.darksoulq.abyssallib.world.level.block.Block.from(event.getBlock()) != null) {
            event.setCancelled(true);
            Item item = com.github.darksoulq.abyssallib.world.level.block.Block.asItem(com.github.darksoulq.abyssallib.world.level.block.Block.from(event.getBlock()));
            if (item == null) return;
            HashMap<Integer, ItemStack> remaining = event.getPlayer().getInventory().addItem(item.stack().clone());
            if (!remaining.isEmpty()) {
                event.getPlayer().getInventory().setItem(EquipmentSlot.HAND, item.stack().clone());
            }
        }
    }

    /**
     * Checks if the block directly below the player's feet is solid.
     */
    private boolean isBlockSolidBelow(Player player) {
        Location loc = player.getLocation().clone().subtract(0, 0.1, 0);
        Block blockBelow = loc.getBlock();
        return blockBelow.getType().isSolid();
    }

    private boolean isWaterBlock(Block block) {
        Material type = block.getType();
        return type == Material.WATER || type == Material.KELP_PLANT || type == Material.SEAGRASS;
    }
}
