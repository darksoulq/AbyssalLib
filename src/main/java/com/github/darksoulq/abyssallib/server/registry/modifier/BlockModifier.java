package com.github.darksoulq.abyssallib.server.registry.modifier;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.BlockPredicate;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import net.kyori.adventure.key.Key;

public class BlockModifier implements DeferredRegistryModifier {
    
    @Override
    public void onRegister(String id, Object value) {
        if (value instanceof CustomBlock block && block.generateItem()) {
            Registries.BLOCK_PREDICATES.register(id, BlockPredicate.builder()
                .id(Key.key(id))
                .build());
            
            Item blockItem = block.getItem().get();
            Registries.ITEM_PREDICATES.register(id, ItemPredicate.builder()
                .id(Key.key(id))
                .build());
            
            Registries.ITEMS.register(id, blockItem);
        }
    }
}