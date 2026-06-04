package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.ExplosiveMinecart;

import java.util.Map;

public class ExplosiveMinecartEntityAdapter extends EntityAdapter<ExplosiveMinecart> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ExplosiveMinecart;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, ExplosiveMinecart value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("fuse_ticks", Codecs.INT, value.getFuseTicks())
            .write("explosion_speed_factor", Codecs.FLOAT, value.getExplosionSpeedFactor());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof ExplosiveMinecart minecart)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("fuse_ticks", Codecs.INT, opt -> opt.ifPresent(minecart::setFuseTicks))
            .readOptional("explosion_speed_factor", Codecs.FLOAT, opt -> opt.ifPresent(minecart::setExplosionSpeedFactor));

        return ctx.result();
    }
}