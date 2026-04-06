package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sittable;

import java.util.Map;

public class SittableEntityAdapter extends EntityAdapter<Sittable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Sittable;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Sittable value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("sitting"), Codecs.BOOLEAN.encode(ops, value.isSitting()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Sittable sittable)) return;
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("sitting")))).onSuccess(sittable::setSitting);
    }
}