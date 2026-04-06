package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AbstractSkeletonEntityAdapter extends EntityAdapter<AbstractSkeleton> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof AbstractSkeleton;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, AbstractSkeleton value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("should_burn_in_day"), Codecs.BOOLEAN.encode(ops, value.shouldBurnInDay()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof AbstractSkeleton skeleton)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("should_burn_in_day")))).onSuccess(skeleton::setShouldBurnInDay);
    }
}