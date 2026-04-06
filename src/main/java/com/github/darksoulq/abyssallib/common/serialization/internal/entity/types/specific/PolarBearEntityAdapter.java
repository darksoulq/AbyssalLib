package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PolarBear;

import java.util.Map;

public class PolarBearEntityAdapter extends EntityAdapter<PolarBear> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof PolarBear;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, PolarBear value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_standing"), Codecs.BOOLEAN.encode(ops, value.isStanding()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof PolarBear bear)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_standing")))).onSuccess(bear::setStanding);
    }
}