package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PiglinAbstract;

import java.util.Map;

public class PiglinAbstractEntityAdapter extends EntityAdapter<PiglinAbstract> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof PiglinAbstract;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, PiglinAbstract value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("immune_to_zombification"), Codecs.BOOLEAN.encode(ops, value.isImmuneToZombification()));
        map.put(ops.createString("conversion_time"), Codecs.INT.encode(ops, value.getConversionTime()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof PiglinAbstract piglin)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("immune_to_zombification")))).onSuccess(piglin::setImmuneToZombification);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("conversion_time")))).onSuccess(piglin::setConversionTime);
    }
}