package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;

import java.util.Map;

public class FireballEntityAdapter extends EntityAdapter<Fireball> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Fireball;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Fireball value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("acceleration", Codecs.VECTOR_F, value.getAcceleration());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Fireball fireball)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("acceleration", Codecs.VECTOR_F, opt -> opt.ifPresent(fireball::setAcceleration));

        return ctx.result();
    }
}