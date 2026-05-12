package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.world.data.attribute.EntityAttributes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AttributeEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        EntityAttributes.of(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        EntityAttributes.unloadIfCached(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        EntityAttributes.of(event.getEntity());
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveEvent event) {
        if (event.getEntity() instanceof Player) return;

        switch (event.getCause()) {
            case DISCARD:
            case DEATH:
            case EXPLODE:
            case HIT:
            case TRANSFORMATION:
                EntityAttributes.of(event.getEntity().getUniqueId()).delete();
            default:
                EntityAttributes.unloadIfCached(event.getEntity().getUniqueId());
                break;
        }
    }
}