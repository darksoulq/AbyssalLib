package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AbstractHorseEntityAdapter extends EntityAdapter<AbstractHorse> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof AbstractHorse;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, AbstractHorse value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("domestication"), Codecs.INT.encode(ops, value.getDomestication()));
        map.put(ops.createString("max_domestication"), Codecs.INT.encode(ops, value.getMaxDomestication()));
        map.put(ops.createString("jump_strength"), Codecs.DOUBLE.encode(ops, value.getJumpStrength()));
        map.put(ops.createString("is_eating_grass"), Codecs.BOOLEAN.encode(ops, value.isEatingGrass()));
        map.put(ops.createString("is_rearing"), Codecs.BOOLEAN.encode(ops, value.isRearing()));
        map.put(ops.createString("is_eating"), Codecs.BOOLEAN.encode(ops, value.isEating()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof AbstractHorse horse)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("domestication")))).onSuccess(horse::setDomestication);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("max_domestication")))).onSuccess(horse::setMaxDomestication);
        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("jump_strength")))).onSuccess(horse::setJumpStrength);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_eating_grass")))).onSuccess(horse::setEatingGrass);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_rearing")))).onSuccess(horse::setRearing);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_eating")))).onSuccess(horse::setEating);
    }
}