package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;

import java.util.Map;

public class SkeletonEntityAdapter extends EntityAdapter<Skeleton> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Skeleton;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Skeleton value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        if (value.isConverting()) {
            ctx.write("conversion_time", Codecs.INT, value.getConversionTime());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Skeleton skeleton)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("conversion_time", Codecs.INT, opt -> opt.ifPresent(skeleton::setConversionTime));

        return ctx.result();
    }
}