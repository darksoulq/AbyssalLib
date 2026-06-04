package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;

import java.util.Map;

public class ChestedHorseEntityAdapter extends EntityAdapter<ChestedHorse> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ChestedHorse;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, ChestedHorse value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_carrying_chest", Codecs.BOOLEAN, value.isCarryingChest());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof ChestedHorse horse)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_carrying_chest", Codecs.BOOLEAN, opt -> opt.ifPresent(horse::setCarryingChest));

        return ctx.result();
    }
}