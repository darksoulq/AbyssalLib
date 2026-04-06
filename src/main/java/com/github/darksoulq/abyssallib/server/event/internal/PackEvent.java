package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.config.internal.PluginConfig;
import com.github.darksoulq.abyssallib.server.command.internal.InternalCommand;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.event.connection.configuration.PlayerConnectionInitialConfigureEvent;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
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
    public void onPlayerConfig(PlayerConnectionInitialConfigureEvent e) {
        if (!AbyssalLib.CONFIG.rp.sendPhase.get().equals(PluginConfig.SendPhase.CONFIGURATION)) return;
        List<ResourcePackInfo> rps = new ArrayList<>();
        InternalCommand.loadRPInfos(rps, false);

        if (!rps.isEmpty()) {
            e.getConnection().getAudience().sendResourcePacks(
                ResourcePackRequest.resourcePackRequest()
                    .packs(rps)
                    .required(true)
                    .build()
            );
        }
    }
}
