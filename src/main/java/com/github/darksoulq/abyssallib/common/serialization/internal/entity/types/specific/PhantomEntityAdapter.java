package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;

import java.util.Map;

public class PhantomEntityAdapter extends EntityAdapter<Phantom> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Phantom;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Phantom value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("size", Codecs.INT, value.getSize())
            .write("should_burn_in_day", Codecs.BOOLEAN, value.shouldBurnInDay())
            .writeNullable("anchor_location", Codecs.LOCATION, value.getAnchorLocation());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Phantom phantom)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("size", Codecs.INT, opt -> opt.ifPresent(phantom::setSize))
            .readOptional("should_burn_in_day", Codecs.BOOLEAN, opt -> opt.ifPresent(phantom::setShouldBurnInDay))
            .readOptional("anchor_location", Codecs.LOCATION, opt -> opt.ifPresent(phantom::setAnchorLocation));

        return ctx.result();
    }
}