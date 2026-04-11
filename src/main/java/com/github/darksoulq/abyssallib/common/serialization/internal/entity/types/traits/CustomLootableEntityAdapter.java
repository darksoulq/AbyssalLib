package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.data.loot.MergeStrategy;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class CustomLootableEntityAdapter extends EntityAdapter<Entity> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity.getPersistentDataContainer().has(new NamespacedKey("abyssallib", "custom_loot_table"), PersistentDataType.STRING);
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Entity value, Map<D, D> map) throws Codec.CodecException {
        String tableId = value.getPersistentDataContainer().get(new NamespacedKey("abyssallib", "custom_loot_table"), PersistentDataType.STRING);
        if (tableId != null) {
            map.put(ops.createString("custom_loot_table"), Codecs.STRING.encode(ops, tableId));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        D tableData = map.get(ops.createString("custom_loot_table"));
        if (tableData != null) {
            Try.of(() -> Codecs.STRING.decode(ops, tableData)).onSuccess(id -> {
                LootTable table = Registries.LOOT_TABLES.get(id);
                if (table != null && table.getMergeStrategy() == MergeStrategy.NONE) {
                    base.getPersistentDataContainer().set(new NamespacedKey("abyssallib", "custom_loot_table"), PersistentDataType.STRING, id);
                }
            });
        }
    }
}