package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PolarBear;

import java.util.Map;

public class PolarBearEntityAdapter extends EntityAdapter<PolarBear> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof PolarBear;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, PolarBear value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_standing", Codecs.BOOLEAN, value.isStanding());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof PolarBear bear)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_standing", Codecs.BOOLEAN, opt -> opt.ifPresent(bear::setStanding));

        return ctx.result();
    }
}