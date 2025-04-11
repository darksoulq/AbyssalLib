package me.darksoul.abyssalLib.event;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.resource.ResourcePack;
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
                    .id(ResourcePack.uuidMap.get(modID))
                    .uri(URI.create(AbyssalLib.PACK_SERVER.getUrl(modID)))
                    .hash(ResourcePack.hashMap.get(modID))
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
