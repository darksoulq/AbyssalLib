package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.config.internal.PluginConfig;
import com.github.darksoulq.abyssallib.server.command.internal.InternalCommand;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.resource.ResourcePackStatus;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class PackEvent {

    @SubscribeEvent
    public void onPLayerJoin(PlayerJoinEvent e) {
        if (!AbyssalLib.CONFIG.rp.sendPhase.get().equals(PluginConfig.SendPhase.JOIN)) return;
        List<ResourcePackInfo> rps = new ArrayList<>();
        InternalCommand.loadRPInfos(rps, false);
        if (!rps.isEmpty()) {
            e.getPlayer().sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                .packs(rps)
                .required(true)
                .build()
            );
        }
    }

    @SubscribeEvent
    public void onPlayerConfig(AsyncPlayerConnectionConfigureEvent e) {
        if (!AbyssalLib.CONFIG.rp.sendPhase.get().equals(PluginConfig.SendPhase.CONFIGURATION)) return;
        List<ResourcePackInfo> rps = new ArrayList<>();
        InternalCommand.loadRPInfos(rps, false);

        if (!rps.isEmpty()) {
            final Object lock = new Object();
            final boolean[] done = {false};
            final int targetPacks = rps.size();
            final int[] completedPacks = {0};

            e.getConnection().getAudience().sendResourcePacks(
                ResourcePackRequest.resourcePackRequest()
                    .packs(rps)
                    .required(true)
                    .callback((uuid, status, audience) -> {
                        if (!status.intermediate()) {
                            synchronized (lock) {
                                completedPacks[0]++;
                                if (completedPacks[0] >= targetPacks) {
                                    done[0] = true;
                                    lock.notifyAll();
                                }
                            }
                        }
                    })
                    .build()
            );

            synchronized (lock) {
                while (!done[0]) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }
}