package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sittable;

import java.util.Map;

public class SittableEntityAdapter extends EntityAdapter<Sittable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Sittable;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Sittable value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("sitting", Codecs.BOOLEAN, value.isSitting());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Sittable sittable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("sitting", Codecs.BOOLEAN, opt -> opt.ifPresent(sittable::setSitting));

        return ctx.result();
    }
}