package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityAttributeChangeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.PlayerStatisticChangeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketSendEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.advancement.Advancement;
import com.github.darksoulq.abyssallib.world.advancement.criterion.AdvancementCriterion;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdvancementEvents {

    private final Map<Class<? extends Event>, List<Advancement>> advancementsByEvent = new ConcurrentHashMap<>();

    private final List<Advancement> globalAdvancements = new CopyOnWriteArrayList<>();

    private final Set<UUID> pendingInventoryChecks = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Long> lastMoveChecks = new ConcurrentHashMap<>();

    public AdvancementEvents() {
        buildCache();
    }

    public void buildCache() {
        advancementsByEvent.clear();
        globalAdvancements.clear();

        for (Advancement adv : Registries.ADVANCEMENTS.getAll().values()) {
            boolean hasGlobal = false;

            for (AdvancementCriterion criterion : adv.getCriteria().values()) {
                Set<Class<? extends Event>> targetEvents = criterion.getTargetEvents();
                if (targetEvents != null && !targetEvents.isEmpty()) {
                    for (Class<? extends Event> eventClass : targetEvents) {
                        advancementsByEvent.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(adv);
                    }
                } else {
                    hasGlobal = true;
                }
            }

            if (hasGlobal || adv.getCriteria().isEmpty()) {
                globalAdvancements.add(adv);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onInventoryUpdate(PacketSendEvent event) {
        Object packet = event.getPacket();
        if (packet instanceof ClientboundContainerSetSlotPacket
            || packet instanceof ClientboundContainerSetContentPacket
            || packet instanceof ClientboundSetPlayerInventoryPacket
            || packet instanceof ClientboundContainerSetDataPacket) {

            Player player = event.getPlayer();

            if (pendingInventoryChecks.add(player.getUniqueId())) {
                AbyssalLib.SCHEDULER.schedule(() -> {
                    pendingInventoryChecks.remove(player.getUniqueId());
                    if (player.isOnline()) {
                        evaluateSpecific(player, event);
                    }
                }).once();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerStatisticChange(PlayerStatisticChangeEvent<?> event) {
        evaluateSpecific(event.getPlayer(), event);
    }

    @SubscribeEvent
    public void onEntityAttributeChange(EntityAttributeChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            evaluateSpecific(player, event);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockBreakEvent event) {
        evaluateSpecific(event.getPlayer(), event);
    }

    @SubscribeEvent
    public void onCraftItem(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            evaluateSpecific(player, event);
        }
    }

    @SubscribeEvent
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player) {
            evaluateSpecific(player, event);
        }
    }

    @SubscribeEvent
    public void onLevelChange(PlayerLevelChangeEvent event) {
        evaluateSpecific(event.getPlayer(), event);
    }

    @SubscribeEvent
    public void onEffectChange(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        evaluateSpecific(player, event);
    }

    @SubscribeEvent
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.hasChangedBlock()) {
            Player player = event.getPlayer();
            long now = System.currentTimeMillis();

            if (now - lastMoveChecks.getOrDefault(player.getUniqueId(), 0L) > 500) {
                lastMoveChecks.put(player.getUniqueId(), now);
                evaluateSpecific(player, event);
            }
        }
    }

    @SubscribeEvent
    public void onJoin(PlayerJoinEvent event) {
        lastMoveChecks.remove(event.getPlayer().getUniqueId());
        pendingInventoryChecks.remove(event.getPlayer().getUniqueId());

        evaluateSpecific(event.getPlayer(), event);
        evaluateGlobal(event.getPlayer(), event);
    }

    private void evaluateSpecific(Player player, Event event) {
        Runnable task = () -> {
            if (!player.isOnline()) return;

            List<Advancement> mapped = advancementsByEvent.get(event.getClass());
            if (mapped != null) {
                for (Advancement customAdv : mapped) {
                    customAdv.evaluate(player, event);
                }
            }
        };

        ensureMainThread(task);
    }

    private void evaluateGlobal(Player player, Event event) {
        Runnable task = () -> {
            if (!player.isOnline()) return;
            for (Advancement customAdv : globalAdvancements) {
                customAdv.evaluate(player, event);
            }
        };

        ensureMainThread(task);
    }

    private void ensureMainThread(Runnable task) {
        if (!Bukkit.isPrimaryThread()) {
            AbyssalLib.SCHEDULER.schedule(task).once();
        } else {
            task.run();
        }
    }
}