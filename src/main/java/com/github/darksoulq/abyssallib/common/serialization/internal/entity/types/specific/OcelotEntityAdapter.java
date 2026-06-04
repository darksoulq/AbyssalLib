package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;

import java.util.Map;

public class OcelotEntityAdapter extends EntityAdapter<Ocelot> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Ocelot;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Ocelot value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_trusting", Codecs.BOOLEAN, value.isTrusting());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Ocelot ocelot)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_trusting", Codecs.BOOLEAN, opt -> opt.ifPresent(ocelot::setTrusting));

        return ctx.result();
    }
}