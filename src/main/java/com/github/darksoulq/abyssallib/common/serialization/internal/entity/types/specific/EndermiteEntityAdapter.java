package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;

import java.util.Map;

public class EndermiteEntityAdapter extends EntityAdapter<Endermite> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Endermite;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Endermite value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("lifetime_ticks", Codecs.INT, value.getLifetimeTicks());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Endermite endermite)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("lifetime_ticks", Codecs.INT, opt -> opt.ifPresent(endermite::setLifetimeTicks));

        return ctx.result();
    }
}