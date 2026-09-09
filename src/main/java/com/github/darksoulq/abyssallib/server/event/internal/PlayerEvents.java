package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockInteractionEvent;
import com.github.darksoulq.abyssallib.server.packet.PacketInterceptor;
import com.github.darksoulq.abyssallib.server.scoreboard.internal.PlayerSidebarManager;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.block.internal.BreakingService;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.entity.player.FakePlayer;
import com.github.darksoulq.abyssallib.world.entity.player.OfflinePlayerData;
import com.github.darksoulq.abyssallib.world.item.Item;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerEvents {

    @SubscribeEvent(ignoreCancelled = false)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        boolean hasFake = FakePlayer.FAKE_PLAYERS.containsKey(event.getUniqueId());
        boolean hasData = OfflinePlayerData.hasData(event.getUniqueId());

        if (hasFake || hasData) {
            try {
                Bukkit.getScheduler().callSyncMethod(AbyssalLib.getInstance(), () -> {
                    FakePlayer fakePlayer = FakePlayer.FAKE_PLAYERS.get(event.getUniqueId());
                    if (fakePlayer != null) {
                        fakePlayer.remove();
                    } else if (hasData) {
                        OfflinePlayerData.forceUnload(event.getUniqueId());
                    }
                    return null;
                }).get();
            } catch (Exception e) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Failed to prepare player data state.");
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onJoin(PlayerJoinEvent event) {
        if (AbyssalLib.CONFIG.features.enableCustomBreakSpeeds.get()) {
            BreakingService.getInstance().storeInitialSpeed(event.getPlayer());
        }
        PacketInterceptor.inject(event.getPlayer());
        PlayerStatistics.of(event.getPlayer());
        if (AbyssalLib.PERMISSION_MANAGER != null) {
            AbyssalLib.PERMISSION_MANAGER.handleJoin(event.getPlayer());
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onLeave(PlayerQuitEvent event) {
        if (AbyssalLib.CONFIG.features.enableCustomBreakSpeeds.get()) {
            BreakingService.getInstance().removeTrackedPlayer(event.getPlayer());
        }
        PacketInterceptor.uninject(event.getPlayer());
        if (AbyssalLib.PERMISSION_MANAGER != null) {
            AbyssalLib.PERMISSION_MANAGER.handleQuit(event.getPlayer());
        }
        PlayerSidebarManager.remove(event.getPlayer());
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPick(PlayerPickBlockEvent event) {
        CustomBlock block = CustomBlock.resolve(event.getBlock());
        if (block != null) {
            event.setCancelled(true);
            Item item = CustomBlock.asItem(block);
            if (item == null) return;
            ItemStack stack = item.getStack().clone();
            HashMap<Integer, ItemStack> remaining = event.getPlayer().getInventory().addItem(stack);
            if (!remaining.isEmpty()) {
                stack = stack.clone();
                stack.setAmount(remaining.values().stream().toList().getFirst().getAmount());
                event.getPlayer().getInventory().setItem(EquipmentSlot.HAND, stack);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (FakePlayer fakePlayer : FakePlayer.FAKE_PLAYERS.values()) {
            Location loc = fakePlayer.getPlayer().getLocation();
            if (loc.getWorld().equals(event.getChunk().getWorld())
                && loc.getChunk().getX() == event.getChunk().getX()
                && loc.getChunk().getZ() == event.getChunk().getZ()) {
                fakePlayer.remove();
            }
        }
    }
}