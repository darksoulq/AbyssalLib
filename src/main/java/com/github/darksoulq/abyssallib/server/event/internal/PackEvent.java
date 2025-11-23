package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PackEvent {
    @SubscribeEvent
    public void onPLayerJoin(PlayerJoinEvent e) {
        List<ResourcePackInfo> rps = new ArrayList<>();
        for (String modID : AbyssalLib.PACK_SERVER.registeredModIDs()) {
            rps.add(ResourcePackInfo.resourcePackInfo()
                    .id(ResourcePack.UUID_MAP.get(modID))
                    .uri(URI.create(AbyssalLib.PACK_SERVER.getUrl(modID)))
                    .hash(ResourcePack.HASH_MAP.get(modID))
                    .build()
            );
        }
        if (!rps.isEmpty()) {
            e.getPlayer().sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                    .packs(rps)
                    .build()
            );
        }
    }
}
