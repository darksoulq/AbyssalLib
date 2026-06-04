package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Bogged;
import org.bukkit.entity.Entity;

import java.util.Map;

public class BoggedEntityAdapter extends EntityAdapter<Bogged> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Bogged;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Bogged value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_sheared", Codecs.BOOLEAN, value.isSheared());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Bogged bogged)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_sheared", Codecs.BOOLEAN, opt -> opt.ifPresent(bogged::setSheared));

        return ctx.result();
    }
}