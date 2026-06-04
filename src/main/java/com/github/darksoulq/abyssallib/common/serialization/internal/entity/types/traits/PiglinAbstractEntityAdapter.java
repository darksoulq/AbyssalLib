package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PiglinAbstract;

import java.util.Map;

public class PiglinAbstractEntityAdapter extends EntityAdapter<PiglinAbstract> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof PiglinAbstract;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, PiglinAbstract value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("immune_to_zombification", Codecs.BOOLEAN, value.isImmuneToZombification())
            .write("conversion_time", Codecs.INT, value.getConversionTime());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof PiglinAbstract piglin)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("immune_to_zombification", Codecs.BOOLEAN, opt -> opt.ifPresent(piglin::setImmuneToZombification))
            .readOptional("conversion_time", Codecs.INT, opt -> opt.ifPresent(piglin::setConversionTime));

        return ctx.result();
    }
}