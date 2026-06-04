package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;

import java.util.Map;

public class SheepEntityAdapter extends EntityAdapter<Sheep> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Sheep;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Sheep value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_sheared", Codecs.BOOLEAN, value.isSheared());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Sheep sheep)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_sheared", Codecs.BOOLEAN, opt -> opt.ifPresent(sheep::setSheared));

        return ctx.result();
    }
}