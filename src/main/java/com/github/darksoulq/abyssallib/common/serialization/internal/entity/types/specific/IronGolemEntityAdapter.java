package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;

import java.util.Map;

public class IronGolemEntityAdapter extends EntityAdapter<IronGolem> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof IronGolem;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, IronGolem value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_player_created", Codecs.BOOLEAN, value.isPlayerCreated());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof IronGolem golem)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_player_created", Codecs.BOOLEAN, opt -> opt.ifPresent(golem::setPlayerCreated));

        return ctx.result();
    }
}