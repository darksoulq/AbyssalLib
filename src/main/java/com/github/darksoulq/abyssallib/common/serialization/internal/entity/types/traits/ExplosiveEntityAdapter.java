package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;

import java.util.Map;

public class ExplosiveEntityAdapter extends EntityAdapter<Explosive> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Explosive;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Explosive value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("yield", Codecs.FLOAT, value.getYield())
            .write("is_incendiary", Codecs.BOOLEAN, value.isIncendiary());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Explosive explosive)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("yield", Codecs.FLOAT, opt -> opt.ifPresent(explosive::setYield))
            .readOptional("is_incendiary", Codecs.BOOLEAN, opt -> opt.ifPresent(explosive::setIsIncendiary));

        return ctx.result();
    }
}