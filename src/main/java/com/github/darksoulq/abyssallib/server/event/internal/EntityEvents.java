package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityDeathEvent;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntityLoadEvent;
import com.github.darksoulq.abyssallib.world.entity.Entity;
import com.github.darksoulq.abyssallib.world.entity.internal.EntityManager;
import org.bukkit.entity.LivingEntity;

public class EntityEvents {

    @SubscribeEvent
    public void onEntityLoad(EntityAddToWorldEvent event) {
        if (!(event.getEntity() instanceof LivingEntity lEntity)) return;
        Entity<? extends LivingEntity> entity = EntityManager.get(lEntity.getUniqueId());
        if (entity == null) return;
        entity.applyGoals();
        entity.applyAttributes();
        entity.applyComponents();
        EventBus.post(new EntityLoadEvent(entity));
        entity.onLoad();
    }

    @SubscribeEvent
    public void onEntityDeath(org.bukkit.event.entity.EntityDeathEvent event) {
        Entity<? extends LivingEntity> entity = Entity.resolve(event.getEntity());
        if (entity == null) return;

        EntityDeathEvent e = new EntityDeathEvent(entity, event.getEntity().getKiller());
        EventBus.post(e);
        if (e.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        if (entity.onDeath(event).equals(ActionResult.CANCEL)) {
            event.setCancelled(true);
            return;
        }

        EntityManager.remove(entity.uuid);
    }

    @SubscribeEvent
    public void onEntityUnload(EntityRemoveFromWorldEvent event) {
        if (!(event.getEntity() instanceof LivingEntity lEntity)) return;
        Entity<? extends LivingEntity> entity = EntityManager.get(lEntity.getUniqueId());
        if (entity == null) return;
        entity.onUnload();
    }
}
