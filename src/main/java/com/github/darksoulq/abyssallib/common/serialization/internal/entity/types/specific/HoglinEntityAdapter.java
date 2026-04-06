package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hoglin;

import java.util.Map;

public class HoglinEntityAdapter extends EntityAdapter<Hoglin> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Hoglin;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Hoglin value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("immune_to_zombification"), Codecs.BOOLEAN.encode(ops, value.isImmuneToZombification()));
        map.put(ops.createString("is_able_to_be_hunted"), Codecs.BOOLEAN.encode(ops, value.isAbleToBeHunted()));

        if (value.isConverting()) {
            map.put(ops.createString("conversion_time"), Codecs.INT.encode(ops, value.getConversionTime()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Hoglin hoglin)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("immune_to_zombification")))).onSuccess(hoglin::setImmuneToZombification);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_able_to_be_hunted")))).onSuccess(hoglin::setIsAbleToBeHunted);

        D conversionData = map.get(ops.createString("conversion_time"));
        if (conversionData != null) {
            Try.of(() -> Codecs.INT.decode(ops, conversionData)).onSuccess(hoglin::setConversionTime);
        }
    }
}