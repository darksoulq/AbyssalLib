package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import java.util.Map;

public class LootableEntityAdapter extends EntityAdapter<Lootable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Lootable;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Lootable value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        if (value.getLootTable() != null) {
            ctx.write("loot_table", Codecs.NAMESPACED_KEY, value.getLootTable().getKey());
        }
        ctx.write("seed", Codecs.LONG, value.getSeed());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Lootable lootable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("loot_table", Codecs.NAMESPACED_KEY, opt -> opt.ifPresent(key -> {
                LootTable table = org.bukkit.Bukkit.getLootTable(key);
                if (table != null) lootable.setLootTable(table);
            }))
            .readOptional("seed", Codecs.LONG, opt -> opt.ifPresent(lootable::setSeed));

        return ctx.result();
    }
}