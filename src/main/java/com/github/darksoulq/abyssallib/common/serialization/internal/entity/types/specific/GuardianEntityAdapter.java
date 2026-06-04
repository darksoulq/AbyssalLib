package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;

import java.util.Map;

public class GuardianEntityAdapter extends EntityAdapter<Guardian> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Guardian;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Guardian value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("has_laser", Codecs.BOOLEAN, value.hasLaser())
            .write("laser_ticks", Codecs.INT, value.getLaserTicks());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Guardian guardian)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("has_laser", Codecs.BOOLEAN, opt -> opt.ifPresent(guardian::setLaser))
            .readOptional("laser_ticks", Codecs.INT, opt -> opt.ifPresent(guardian::setLaserTicks));

        return ctx.result();
    }
}