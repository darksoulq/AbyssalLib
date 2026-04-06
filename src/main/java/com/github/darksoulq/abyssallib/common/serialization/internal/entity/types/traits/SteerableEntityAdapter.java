package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Steerable;

import java.util.Map;

public class SteerableEntityAdapter extends EntityAdapter<Steerable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Steerable;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Steerable value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("has_saddle"), Codecs.BOOLEAN.encode(ops, value.hasSaddle()));
        map.put(ops.createString("boost_ticks"), Codecs.INT.encode(ops, value.getBoostTicks()));
        map.put(ops.createString("current_boost_ticks"), Codecs.INT.encode(ops, value.getCurrentBoostTicks()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Steerable steerable)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_saddle")))).onSuccess(steerable::setSaddle);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("boost_ticks")))).onSuccess(steerable::setBoostTicks);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("current_boost_ticks")))).onSuccess(steerable::setCurrentBoostTicks);
    }
}