package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.server.command.internal.InternalCommand;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class PackEvent {
    @SubscribeEvent
    public void onPLayerJoin(PlayerJoinEvent e) {
        List<ResourcePackInfo> rps = new ArrayList<>();
        InternalCommand.loadRPInfos(rps);
        if (!rps.isEmpty()) {
            e.getPlayer().sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                    .packs(rps)
                    .build()
            );
        }
    }
}
