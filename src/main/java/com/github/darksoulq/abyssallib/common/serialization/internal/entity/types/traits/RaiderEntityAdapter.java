package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Raider;

import java.util.Map;

public class RaiderEntityAdapter extends EntityAdapter<Raider> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Raider;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Raider value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("wave", Codecs.INT, value.getWave())
            .write("is_patrol_leader", Codecs.BOOLEAN, value.isPatrolLeader())
            .write("can_join_raid", Codecs.BOOLEAN, value.isCanJoinRaid())
            .write("ticks_outside_raid", Codecs.INT, value.getTicksOutsideRaid())
            .write("is_celebrating", Codecs.BOOLEAN, value.isCelebrating());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Raider raider)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("wave", Codecs.INT, opt -> opt.ifPresent(raider::setWave))
            .readOptional("is_patrol_leader", Codecs.BOOLEAN, opt -> opt.ifPresent(raider::setPatrolLeader))
            .readOptional("can_join_raid", Codecs.BOOLEAN, opt -> opt.ifPresent(raider::setCanJoinRaid))
            .readOptional("ticks_outside_raid", Codecs.INT, opt -> opt.ifPresent(raider::setTicksOutsideRaid))
            .readOptional("is_celebrating", Codecs.BOOLEAN, opt -> opt.ifPresent(raider::setCelebrating));

        return ctx.result();
    }
}