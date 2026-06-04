package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;

import java.util.Map;

public class SlimeEntityAdapter extends EntityAdapter<Slime> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Slime;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Slime value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("slime_size", Codecs.INT, value.getSize())
            .write("can_wander", Codecs.BOOLEAN, value.canWander());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Slime slime)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("slime_size", Codecs.INT, opt -> opt.ifPresent(slime::setSize))
            .readOptional("can_wander", Codecs.BOOLEAN, opt -> opt.ifPresent(slime::setWander));

        return ctx.result();
    }
}