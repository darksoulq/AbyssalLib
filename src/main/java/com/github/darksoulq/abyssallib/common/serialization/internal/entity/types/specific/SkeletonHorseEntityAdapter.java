package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SkeletonHorse;

import java.util.Map;

public class SkeletonHorseEntityAdapter extends EntityAdapter<SkeletonHorse> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof SkeletonHorse;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, SkeletonHorse value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("is_trapped", Codecs.BOOLEAN, value.isTrapped())
            .write("trap_time", Codecs.INT, value.getTrapTime());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof SkeletonHorse horse)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_trapped", Codecs.BOOLEAN, opt -> opt.ifPresent(horse::setTrapped))
            .readOptional("trap_time", Codecs.INT, opt -> opt.ifPresent(horse::setTrapTime));

        return ctx.result();
    }
}