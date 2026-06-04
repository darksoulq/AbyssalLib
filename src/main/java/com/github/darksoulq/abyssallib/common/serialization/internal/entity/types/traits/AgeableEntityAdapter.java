package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AgeableEntityAdapter extends EntityAdapter<Ageable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Ageable;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Ageable value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("age", Codecs.INT, value.getAge())
            .write("is_adult", Codecs.BOOLEAN, value.isAdult());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Ageable ageable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("age", Codecs.INT, opt -> {
            if (opt.isPresent()) {
                ageable.setAge(opt.get());
            } else {
                ctx.readOptional("is_adult", Codecs.BOOLEAN, adultOpt -> adultOpt.ifPresent(adult -> {
                    if (adult) ageable.setAdult();
                    else ageable.setBaby();
                }));
            }
        });

        return ctx.result();
    }
}