package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;

import java.util.Map;

public class CreeperEntityAdapter extends EntityAdapter<Creeper> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Creeper;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Creeper value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("powered", Codecs.BOOLEAN, value.isPowered())
            .write("ignited", Codecs.BOOLEAN, value.isIgnited())
            .write("max_fuse_ticks", Codecs.INT, value.getMaxFuseTicks())
            .write("fuse_ticks", Codecs.INT, value.getFuseTicks())
            .write("explosion_radius", Codecs.INT, value.getExplosionRadius());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Creeper creeper)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("powered", Codecs.BOOLEAN, opt -> opt.ifPresent(creeper::setPowered))
            .readOptional("ignited", Codecs.BOOLEAN, opt -> opt.ifPresent(creeper::setIgnited))
            .readOptional("max_fuse_ticks", Codecs.INT, opt -> opt.ifPresent(creeper::setMaxFuseTicks))
            .readOptional("fuse_ticks", Codecs.INT, opt -> opt.ifPresent(creeper::setFuseTicks))
            .readOptional("explosion_radius", Codecs.INT, opt -> opt.ifPresent(creeper::setExplosionRadius));

        return ctx.result();
    }
}