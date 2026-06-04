package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowman;

import java.util.Map;

public class SnowmanEntityAdapter extends EntityAdapter<Snowman> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Snowman;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Snowman value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_derp", Codecs.BOOLEAN, value.isDerp());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Snowman snowman)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_derp", Codecs.BOOLEAN, opt -> opt.ifPresent(snowman::setDerp));

        return ctx.result();
    }
}