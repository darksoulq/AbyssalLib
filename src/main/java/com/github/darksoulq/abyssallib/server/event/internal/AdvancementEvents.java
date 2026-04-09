package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityAttributeChangeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketSendEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class AdvancementEvents {

    @SubscribeEvent(ignoreCancelled = false)
    public void onInventoryUpdate(PacketSendEvent event) {
        if (event.getPacket() instanceof ClientboundContainerSetSlotPacket
            || event.getPacket() instanceof ClientboundContainerSetContentPacket
            || event.getPacket() instanceof ClientboundSetPlayerInventoryPacket
            || event.getPacket() instanceof ClientboundContainerSetDataPacket) {
            checkAdvancements(event.getPlayer(), event);
        }
    }

    @SubscribeEvent
    public void onPlayerStatisticChange(PlayerStatisticChangeEvent<?> event) {
        checkAdvancements(event.getPlayer(), event);
    }

    @SubscribeEvent
    public void onEntityAttributeChange(EntityAttributeChangeEvent<?> event) {
        if (event.getEntity() instanceof Player player) {
            checkAdvancements(player, event);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockBreakEvent event) {
        checkAdvancements(event.getPlayer(), event);
    }

    @SubscribeEvent
    public void onCraftItem(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            checkAdvancements(player, event);
        }
    }

    @SubscribeEvent
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player) {
            checkAdvancements(player, event);
        }
    }

    @SubscribeEvent
    public void onLevelChange(PlayerLevelChangeEvent event) {
        checkAdvancements(event.getPlayer(), event);
    }

    @SubscribeEvent
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.hasChangedBlock()) {
            checkAdvancements(event.getPlayer(), event);
        }
    }

    @SubscribeEvent
    public void onJoin(PlayerJoinEvent event) {
        checkAdvancements(event.getPlayer(), event);
    }

    private void checkAdvancements(Player player, Event event) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(AbyssalLib.getInstance(), () -> {
                if (player.isOnline()) {
                    Registries.ADVANCEMENTS.getAll().values().forEach(customAdv -> customAdv.evaluate(player, event));
                }
            });
        } else {
            Registries.ADVANCEMENTS.getAll().values().forEach(customAdv -> customAdv.evaluate(player, event));
        }
    }
}