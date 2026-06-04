package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.GlowSquid;

import java.util.Map;

public class GlowSquidEntityAdapter extends EntityAdapter<GlowSquid> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof GlowSquid;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, GlowSquid value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("dark_ticks_remaining", Codecs.INT, value.getDarkTicksRemaining());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof GlowSquid squid)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("dark_ticks_remaining", Codecs.INT, opt -> opt.ifPresent(squid::setDarkTicksRemaining));

        return ctx.result();
    }
}