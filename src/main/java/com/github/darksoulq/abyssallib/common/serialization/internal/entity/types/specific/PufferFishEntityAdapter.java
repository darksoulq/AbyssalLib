package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PufferFish;

import java.util.Map;

public class PufferFishEntityAdapter extends EntityAdapter<PufferFish> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof PufferFish;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, PufferFish value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("puff_state", Codecs.INT, value.getPuffState());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof PufferFish fish)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("puff_state", Codecs.INT, opt -> opt.ifPresent(fish::setPuffState));

        return ctx.result();
    }
}