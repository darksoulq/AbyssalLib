package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AbstractSkeletonEntityAdapter extends EntityAdapter<AbstractSkeleton> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof AbstractSkeleton;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, AbstractSkeleton value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("should_burn_in_day", Codecs.BOOLEAN, value.shouldBurnInDay());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof AbstractSkeleton skeleton)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("should_burn_in_day", Codecs.BOOLEAN, opt -> opt.ifPresent(skeleton::setShouldBurnInDay));

        return ctx.result();
    }
}