package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AbstractHorseEntityAdapter extends EntityAdapter<AbstractHorse> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof AbstractHorse;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, AbstractHorse value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("domestication", Codecs.INT, value.getDomestication())
            .write("max_domestication", Codecs.INT, value.getMaxDomestication())
            .write("jump_strength", Codecs.DOUBLE, value.getJumpStrength())
            .write("is_eating_grass", Codecs.BOOLEAN, value.isEatingGrass())
            .write("is_rearing", Codecs.BOOLEAN, value.isRearing())
            .write("is_eating", Codecs.BOOLEAN, value.isEating());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof AbstractHorse horse)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("domestication", Codecs.INT, opt -> opt.ifPresent(horse::setDomestication))
            .readOptional("max_domestication", Codecs.INT, opt -> opt.ifPresent(horse::setMaxDomestication))
            .readOptional("jump_strength", Codecs.DOUBLE, opt -> opt.ifPresent(horse::setJumpStrength))
            .readOptional("is_eating_grass", Codecs.BOOLEAN, opt -> opt.ifPresent(horse::setEatingGrass))
            .readOptional("is_rearing", Codecs.BOOLEAN, opt -> opt.ifPresent(horse::setRearing))
            .readOptional("is_eating", Codecs.BOOLEAN, opt -> opt.ifPresent(horse::setEating));

        return ctx.result();
    }
}