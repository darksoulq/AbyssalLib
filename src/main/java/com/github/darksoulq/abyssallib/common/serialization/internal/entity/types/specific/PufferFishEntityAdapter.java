package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PufferFish;

import java.util.Map;

public class PufferFishEntityAdapter extends EntityAdapter<PufferFish> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof PufferFish;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, PufferFish value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("puff_state"), Codecs.INT.encode(ops, value.getPuffState()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof PufferFish fish)) return;
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("puff_state")))).onSuccess(fish::setPuffState);
    }
}