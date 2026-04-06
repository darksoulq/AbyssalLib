package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;

import java.util.Map;

public class SkeletonEntityAdapter extends EntityAdapter<Skeleton> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Skeleton;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Skeleton value, Map<D, D> map) throws Codec.CodecException {
        if (value.isConverting()) {
            map.put(ops.createString("conversion_time"), Codecs.INT.encode(ops, value.getConversionTime()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Skeleton skeleton)) return;

        D conversionData = map.get(ops.createString("conversion_time"));
        if (conversionData != null) {
            Try.of(() -> Codecs.INT.decode(ops, conversionData)).onSuccess(skeleton::setConversionTime);
        }
    }
}