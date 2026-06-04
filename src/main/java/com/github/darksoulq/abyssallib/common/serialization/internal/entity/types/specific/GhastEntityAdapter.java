package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;

import java.util.Map;

public class GhastEntityAdapter extends EntityAdapter<Ghast> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Ghast;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Ghast value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("is_charging", Codecs.BOOLEAN, value.isCharging())
            .write("explosion_power", Codecs.INT, value.getExplosionPower());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Ghast ghast)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_charging", Codecs.BOOLEAN, opt -> opt.ifPresent(ghast::setCharging))
            .readOptional("explosion_power", Codecs.INT, opt -> opt.ifPresent(ghast::setExplosionPower));

        return ctx.result();
    }
}