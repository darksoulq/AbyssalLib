package com.github.darksoulq.abyssallib.server.registry.modifier;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.data.loot.MergeStrategy;

public class LootTableModifier implements DeferredRegistryModifier {

    @Override
    public void onRegister(String id, Object value) {
        if (value instanceof LootTable table) {
            String targetId = table.getVanillaId() != null ? table.getVanillaId() : id;
            LootTable existing = Registries.LOOT_TABLES.get(targetId);

            if (table.getMergeStrategy() == MergeStrategy.REPLACE) {
                if (existing != null) {
                    Registries.LOOT_TABLES.remove(targetId);
                }
                Registries.LOOT_TABLES.register(targetId, table);
            } else if (table.getMergeStrategy() == MergeStrategy.MERGE) {
                if (existing != null) {
                    existing.getPools().addAll(table.getPools());
                } else {
                    Registries.LOOT_TABLES.register(targetId, table);
                }
            } else {
                Registries.LOOT_TABLES.register(targetId, table);
            }
        }
    }
}