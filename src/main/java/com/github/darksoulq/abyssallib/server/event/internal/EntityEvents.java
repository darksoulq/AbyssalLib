package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityDeathEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityLoadEvent;
import com.github.darksoulq.abyssallib.world.entity.Entity;
import com.github.darksoulq.abyssallib.world.entity.internal.EntityManager;
import org.bukkit.entity.LivingEntity;

public class EntityEvents {

    @SubscribeEvent
    public void onEntityLoad(EntityAddToWorldEvent event) {
        if (event.getEntity() instanceof LivingEntity lEntity) {
            Entity<? extends LivingEntity> entity = EntityManager.get(event.getEntity().getUniqueId());
            if (entity != null) {
                entity.applyGoals(lEntity);
                entity.applyAttributes(lEntity);
                AbyssalLib.EVENT_BUS.post(new EntityLoadEvent(entity));
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(org.bukkit.event.entity.EntityDeathEvent event) {
        Entity<? extends LivingEntity> entity = Entity.from(event.getEntity());
        if (entity == null) return;

        EntityDeathEvent e = new EntityDeathEvent(entity, event.getEntity().getKiller());
        AbyssalLib.EVENT_BUS.post(e);
        if (e.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        EntityManager.remove(entity.uuid);
    }
}
