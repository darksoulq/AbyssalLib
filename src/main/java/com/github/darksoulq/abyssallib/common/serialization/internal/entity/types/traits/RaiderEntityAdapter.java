package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Raider;

import java.util.Map;

public class RaiderEntityAdapter extends EntityAdapter<Raider> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Raider;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Raider value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("wave"), Codecs.INT.encode(ops, value.getWave()));
        map.put(ops.createString("is_patrol_leader"), Codecs.BOOLEAN.encode(ops, value.isPatrolLeader()));
        map.put(ops.createString("can_join_raid"), Codecs.BOOLEAN.encode(ops, value.isCanJoinRaid()));
        map.put(ops.createString("ticks_outside_raid"), Codecs.INT.encode(ops, value.getTicksOutsideRaid()));
        map.put(ops.createString("is_celebrating"), Codecs.BOOLEAN.encode(ops, value.isCelebrating()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Raider raider)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("wave")))).onSuccess(raider::setWave);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_patrol_leader")))).onSuccess(raider::setPatrolLeader);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_join_raid")))).onSuccess(raider::setCanJoinRaid);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("ticks_outside_raid")))).onSuccess(raider::setTicksOutsideRaid);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_celebrating")))).onSuccess(raider::setCelebrating);
    }
}