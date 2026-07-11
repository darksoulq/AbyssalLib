package com.github.darksoulq.abyssallib.server.registry.modifier;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.entity.EntityPredicate;
import net.kyori.adventure.key.Key;

public class EntityModifier implements DeferredRegistryModifier {

    @Override
    public void onRegister(String id, Object value) {
        if (value instanceof CustomEntity<?>) {
            Registries.ENTITY_PREDICATES.register(id, EntityPredicate.builder()
                .id(Key.key(id))
                .build());
        }
    }
}