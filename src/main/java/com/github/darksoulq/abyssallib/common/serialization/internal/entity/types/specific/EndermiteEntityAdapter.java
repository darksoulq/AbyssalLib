package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;

import java.util.Map;

public class EndermiteEntityAdapter extends EntityAdapter<Endermite> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Endermite;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Endermite value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("lifetime_ticks"), Codecs.INT.encode(ops, value.getLifetimeTicks()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Endermite endermite)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("lifetime_ticks")))).onSuccess(endermite::setLifetimeTicks);
    }
}