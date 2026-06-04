package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.destroystokyo.paper.loottable.LootableInventory;
import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;

import java.util.Map;

public class LootableInventoryEntityAdapter extends EntityAdapter<LootableInventory> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof LootableInventory;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, LootableInventory value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("next_refill", Codecs.LONG, value.getNextRefill());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof LootableInventory lootable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("next_refill", Codecs.LONG, opt -> opt.ifPresent(lootable::setNextRefill));

        return ctx.result();
    }
}