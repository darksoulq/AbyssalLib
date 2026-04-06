package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;

import java.util.Map;

public class GuardianEntityAdapter extends EntityAdapter<Guardian> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Guardian;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Guardian value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("has_laser"), Codecs.BOOLEAN.encode(ops, value.hasLaser()));
        map.put(ops.createString("laser_ticks"), Codecs.INT.encode(ops, value.getLaserTicks()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Guardian guardian)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_laser")))).onSuccess(guardian::setLaser);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("laser_ticks")))).onSuccess(guardian::setLaserTicks);
    }
}