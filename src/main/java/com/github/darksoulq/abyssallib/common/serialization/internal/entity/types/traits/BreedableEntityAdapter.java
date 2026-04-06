package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Entity;

import java.util.Map;

public class BreedableEntityAdapter extends EntityAdapter<Breedable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Breedable;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Breedable value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("age_locked"), Codecs.BOOLEAN.encode(ops, value.getAgeLock()));
        map.put(ops.createString("can_breed"), Codecs.BOOLEAN.encode(ops, value.canBreed()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Breedable breedable)) return;
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("age_locked")))).onSuccess(breedable::setAgeLock);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_breed")))).onSuccess(breedable::setBreed);
    }
}