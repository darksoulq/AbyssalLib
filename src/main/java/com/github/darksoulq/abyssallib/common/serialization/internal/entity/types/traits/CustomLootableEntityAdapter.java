package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Entity value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        String tableId = value.getPersistentDataContainer().get(new NamespacedKey("abyssallib", "custom_loot_table"), PersistentDataType.STRING);
        if (tableId != null) {
            ctx.write("custom_loot_table", Codecs.STRING, tableId);
        }
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("custom_loot_table", Codecs.STRING, opt -> opt.ifPresent(id -> {
            LootTable table = Registries.LOOT_TABLES.get(id);
            if (table != null && table.getMergeStrategy() == MergeStrategy.NONE) {
                base.getPersistentDataContainer().set(new NamespacedKey("abyssallib", "custom_loot_table"), PersistentDataType.STRING, id);
            }
        }));

        return ctx.result();
    }
}