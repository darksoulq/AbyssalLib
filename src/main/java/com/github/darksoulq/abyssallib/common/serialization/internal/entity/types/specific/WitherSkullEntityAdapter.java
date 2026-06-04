package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.WitherSkull;

import java.util.Map;

public class WitherSkullEntityAdapter extends EntityAdapter<WitherSkull> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof WitherSkull;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, WitherSkull value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_charged", Codecs.BOOLEAN, value.isCharged());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof WitherSkull skull)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_charged", Codecs.BOOLEAN, opt -> opt.ifPresent(skull::setCharged));

        return ctx.result();
    }
}