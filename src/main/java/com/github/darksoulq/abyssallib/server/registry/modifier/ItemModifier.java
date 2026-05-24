package com.github.darksoulq.abyssallib.server.registry.modifier;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import net.kyori.adventure.key.Key;

public class ItemModifier implements DeferredRegistryModifier {
    
    @Override
    public void onRegister(String id, Object value) {
        if (value instanceof Item) {
            Registries.ITEM_PREDICATES.register(id, ItemPredicate.builder()
                .id(Key.key(id))
                .build());
        }
    }
}