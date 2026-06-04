package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Turtle;

import java.util.Map;

public class TurtleEntityAdapter extends EntityAdapter<Turtle> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Turtle;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Turtle value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("has_egg", Codecs.BOOLEAN, value.hasEgg())
            .write("home", Codecs.LOCATION, value.getHome());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Turtle turtle)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("has_egg", Codecs.BOOLEAN, opt -> opt.ifPresent(turtle::setHasEgg))
            .readOptional("home", Codecs.LOCATION, opt -> opt.ifPresent(turtle::setHome));

        return ctx.result();
    }
}