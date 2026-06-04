package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Steerable;

import java.util.Map;

public class SteerableEntityAdapter extends EntityAdapter<Steerable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Steerable;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Steerable value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("has_saddle", Codecs.BOOLEAN, value.hasSaddle())
            .write("boost_ticks", Codecs.INT, value.getBoostTicks())
            .write("current_boost_ticks", Codecs.INT, value.getCurrentBoostTicks());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Steerable steerable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("has_saddle", Codecs.BOOLEAN, opt -> opt.ifPresent(steerable::setSaddle))
            .readOptional("boost_ticks", Codecs.INT, opt -> opt.ifPresent(steerable::setBoostTicks))
            .readOptional("current_boost_ticks", Codecs.INT, opt -> opt.ifPresent(steerable::setCurrentBoostTicks));

        return ctx.result();
    }
}