package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;

import java.util.Map;

public class EnderSignalEntityAdapter extends EntityAdapter<EnderSignal> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof EnderSignal;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, EnderSignal value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("drop_item", Codecs.BOOLEAN, value.getDropItem())
            .write("despawn_timer", Codecs.INT, value.getDespawnTimer())
            .write("item", Codecs.ITEM_STACK, value.getItem())
            .writeNullable("target_location", Codecs.LOCATION, value.getTargetLocation());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof EnderSignal signal)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("drop_item", Codecs.BOOLEAN, opt -> opt.ifPresent(signal::setDropItem))
            .readOptional("despawn_timer", Codecs.INT, opt -> opt.ifPresent(signal::setDespawnTimer))
            .readOptional("item", Codecs.ITEM_STACK, opt -> opt.ifPresent(signal::setItem))
            .readOptional("target_location", Codecs.LOCATION, opt -> opt.ifPresent(loc -> signal.setTargetLocation(loc, false)));

        return ctx.result();
    }
}