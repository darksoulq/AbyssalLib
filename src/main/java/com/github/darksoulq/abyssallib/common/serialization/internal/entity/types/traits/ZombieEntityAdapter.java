package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;

import java.util.Map;

public class ZombieEntityAdapter extends EntityAdapter<Zombie> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Zombie;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Zombie value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("can_break_doors", Codecs.BOOLEAN, value.canBreakDoors())
            .write("should_burn_in_day", Codecs.BOOLEAN, value.shouldBurnInDay());

        if (value.isConverting()) {
            ctx.write("conversion_time", Codecs.INT, value.getConversionTime());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Zombie zombie)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("can_break_doors", Codecs.BOOLEAN, opt -> opt.ifPresent(zombie::setCanBreakDoors))
            .readOptional("should_burn_in_day", Codecs.BOOLEAN, opt -> opt.ifPresent(zombie::setShouldBurnInDay))
            .readOptional("conversion_time", Codecs.INT, opt -> opt.ifPresent(zombie::setConversionTime));

        return ctx.result();
    }
}