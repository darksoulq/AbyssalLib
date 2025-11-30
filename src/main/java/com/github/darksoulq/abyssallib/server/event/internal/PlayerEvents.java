package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockInteractionEvent;
import com.github.darksoulq.abyssallib.server.packet.PacketInterceptor;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.item.Item;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerEvents {
    @SubscribeEvent(ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        CustomBlock block = CustomBlock.from(event.getClickedBlock());
        if (block == null) return;
        BlockInteractionEvent be = EventBus.post(new BlockInteractionEvent(
                event.getPlayer(),
                block,
                event.getBlockFace(),
                event.getInteractionPoint(),
                event.getAction(),
                event.getItem()
        ));
        if (be.isCancelled()) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onJoin(PlayerJoinEvent event) {
        PacketInterceptor.inject(event.getPlayer());
        EntityAttributes.of(event.getPlayer());
        PlayerStatistics.of(event.getPlayer());
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onLeave(PlayerQuitEvent event) {
        PacketInterceptor.uninject(event.getPlayer());
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPick(PlayerPickBlockEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) {
            event.setCancelled(true);
            Item item = CustomBlock.asItem(CustomBlock.from(event.getBlock()));
            if (item == null) return;
            ItemStack stack = item.getStack().clone();
            HashMap<Integer, ItemStack> remaining = event.getPlayer().getInventory().addItem(stack);
            if (!remaining.isEmpty()) {
                stack = stack.clone();
                stack.setAmount(remaining.values().stream().toList().getFirst().getAmount());
                event.getPlayer().getInventory().setItem(EquipmentSlot.HAND,
                        stack);
            }
        }
    }
}
