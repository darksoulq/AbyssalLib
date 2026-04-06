package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Creaking;
import org.bukkit.entity.Entity;

import java.util.Map;

public class CreakingEntityAdapter extends EntityAdapter<Creaking> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Creaking;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Creaking value, Map<D, D> map) throws Codec.CodecException {
        if (value.getHome() != null) {
            map.put(ops.createString("home"), Codecs.LOCATION.encode(ops, value.getHome()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
    }
}