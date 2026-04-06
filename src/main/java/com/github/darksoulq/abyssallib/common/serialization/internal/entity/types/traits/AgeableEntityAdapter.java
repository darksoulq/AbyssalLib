package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AgeableEntityAdapter extends EntityAdapter<Ageable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Ageable;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Ageable value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("age"), Codecs.INT.encode(ops, value.getAge()));
        map.put(ops.createString("is_adult"), Codecs.BOOLEAN.encode(ops, value.isAdult()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Ageable ageable)) return;

        D ageData = map.get(ops.createString("age"));
        if (ageData != null) {
            Try.of(() -> Codecs.INT.decode(ops, ageData)).onSuccess(ageable::setAge);
        } else {
            D adultData = map.get(ops.createString("is_adult"));
            if (adultData != null) {
                Try.of(() -> Codecs.BOOLEAN.decode(ops, adultData)).onSuccess(isAdult -> {
                    if (isAdult) ageable.setAdult();
                    else ageable.setBaby();
                });
            }
        }
    }
}