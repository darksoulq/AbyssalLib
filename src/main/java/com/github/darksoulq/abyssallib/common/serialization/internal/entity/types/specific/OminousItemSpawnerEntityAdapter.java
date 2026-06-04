package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.OminousItemSpawner;

import java.util.Map;

public class OminousItemSpawnerEntityAdapter extends EntityAdapter<OminousItemSpawner> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof OminousItemSpawner;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, OminousItemSpawner value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("spawn_item_after_ticks", Codecs.LONG, value.getSpawnItemAfterTicks())
            .write("item", Codecs.ITEM_STACK, value.getItem());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof OminousItemSpawner spawner)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("spawn_item_after_ticks", Codecs.LONG, opt -> opt.ifPresent(spawner::setSpawnItemAfterTicks))
            .readOptional("item", Codecs.ITEM_STACK, opt -> opt.ifPresent(spawner::setItem));

        return ctx.result();
    }
}