package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AnimalsEntityAdapter extends EntityAdapter<Animals> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Animals;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Animals value, Map<D, D> map) throws Codec.CodecException {
        if (value.getBreedCause() != null) {
            map.put(ops.createString("breed_cause"), Codecs.UUID.encode(ops, value.getBreedCause()));
        }
        map.put(ops.createString("love_mode_ticks"), Codecs.INT.encode(ops, value.getLoveModeTicks()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Animals animals)) return;

        D causeData = map.get(ops.createString("breed_cause"));
        if (causeData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, causeData)).onSuccess(animals::setBreedCause);
        }
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("love_mode_ticks")))).onSuccess(animals::setLoveModeTicks);
    }
}