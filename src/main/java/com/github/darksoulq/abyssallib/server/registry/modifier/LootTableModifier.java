package com.github.darksoulq.abyssallib.server.registry.modifier;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.data.loot.MergeStrategy;

public class LootTableModifier implements DeferredRegistryModifier {

    @Override
    public void onRegister(String id, Object value) {
        if (value instanceof LootTable table) {
            LootTable existing = Registries.LOOT_TABLES.get(id);

            if (table.mergeStrategy() == MergeStrategy.REPLACE) {
                if (existing != null) {
                    Registries.LOOT_TABLES.remove(id);
                }
                Registries.LOOT_TABLES.register(id, table);
            } else if (table.mergeStrategy() == MergeStrategy.MERGE) {
                if (existing != null) {
                    existing.pools().addAll(table.pools());
                } else {
                    Registries.LOOT_TABLES.register(id, table);
                }
            } else {
                Registries.LOOT_TABLES.register(id, table);
            }
        }
    }
}