package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Bogged;
import org.bukkit.entity.Entity;

import java.util.Map;

public class BoggedEntityAdapter extends EntityAdapter<Bogged> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Bogged;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Bogged value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_sheared"), Codecs.BOOLEAN.encode(ops, value.isSheared()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Bogged bogged)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_sheared")))).onSuccess(bogged::setSheared);
    }
}