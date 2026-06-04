package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AllayEntityAdapter extends EntityAdapter<Allay> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Allay;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Allay value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("can_duplicate", Codecs.BOOLEAN, value.canDuplicate())
            .write("duplication_cooldown", Codecs.LONG, value.getDuplicationCooldown());

        if (value.isDancing()) {
            ctx.write("is_dancing", Codecs.BOOLEAN, true);
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Allay allay)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("can_duplicate", Codecs.BOOLEAN, opt -> opt.ifPresent(allay::setCanDuplicate))
            .readOptional("duplication_cooldown", Codecs.LONG, opt -> opt.ifPresent(allay::setDuplicationCooldown))
            .readOptional("is_dancing", Codecs.BOOLEAN, opt -> opt.ifPresent(dancing -> {
                if (dancing) allay.startDancing();
            }));

        return ctx.result();
    }
}