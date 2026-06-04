package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ravager;

import java.util.Map;

public class RavagerEntityAdapter extends EntityAdapter<Ravager> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Ravager;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Ravager value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("attack_ticks", Codecs.INT, value.getAttackTicks())
            .write("stunned_ticks", Codecs.INT, value.getStunnedTicks())
            .write("roar_ticks", Codecs.INT, value.getRoarTicks());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Ravager ravager)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("attack_ticks", Codecs.INT, opt -> opt.ifPresent(ravager::setAttackTicks))
            .readOptional("stunned_ticks", Codecs.INT, opt -> opt.ifPresent(ravager::setStunnedTicks))
            .readOptional("roar_ticks", Codecs.INT, opt -> opt.ifPresent(ravager::setRoarTicks));

        return ctx.result();
    }
}