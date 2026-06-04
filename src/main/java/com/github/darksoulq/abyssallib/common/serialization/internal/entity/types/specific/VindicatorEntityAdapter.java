package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vindicator;

import java.util.Map;

public class VindicatorEntityAdapter extends EntityAdapter<Vindicator> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Vindicator;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Vindicator value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_johnny", Codecs.BOOLEAN, value.isJohnny());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Vindicator vindicator)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_johnny", Codecs.BOOLEAN, opt -> opt.ifPresent(vindicator::setJohnny));

        return ctx.result();
    }
}