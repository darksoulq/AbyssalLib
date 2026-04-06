package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SkeletonHorse;

import java.util.Map;

public class SkeletonHorseEntityAdapter extends EntityAdapter<SkeletonHorse> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof SkeletonHorse;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, SkeletonHorse value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_trapped"), Codecs.BOOLEAN.encode(ops, value.isTrapped()));
        map.put(ops.createString("trap_time"), Codecs.INT.encode(ops, value.getTrapTime()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof SkeletonHorse horse)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_trapped")))).onSuccess(horse::setTrapped);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("trap_time")))).onSuccess(horse::setTrapTime);
    }
}