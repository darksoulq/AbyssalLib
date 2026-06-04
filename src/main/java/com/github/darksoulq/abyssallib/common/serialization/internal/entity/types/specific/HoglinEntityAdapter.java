package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hoglin;

import java.util.Map;

public class HoglinEntityAdapter extends EntityAdapter<Hoglin> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Hoglin;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Hoglin value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("immune_to_zombification", Codecs.BOOLEAN, value.isImmuneToZombification())
            .write("is_able_to_be_hunted", Codecs.BOOLEAN, value.isAbleToBeHunted());

        if (value.isConverting()) {
            ctx.write("conversion_time", Codecs.INT, value.getConversionTime());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Hoglin hoglin)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("immune_to_zombification", Codecs.BOOLEAN, opt -> opt.ifPresent(hoglin::setImmuneToZombification))
            .readOptional("is_able_to_be_hunted", Codecs.BOOLEAN, opt -> opt.ifPresent(hoglin::setIsAbleToBeHunted))
            .readOptional("conversion_time", Codecs.INT, opt -> opt.ifPresent(hoglin::setConversionTime));

        return ctx.result();
    }
}