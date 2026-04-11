package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;

import java.util.Map;

public class FireballEntityAdapter extends EntityAdapter<Fireball> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Fireball;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Fireball value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("direction"), Codecs.VECTOR_F.encode(ops, value.getDirection()));
        map.put(ops.createString("acceleration"), Codecs.VECTOR_F.encode(ops, value.getAcceleration()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Fireball fireball)) return;

        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("direction")))).onSuccess(fireball::setDirection);
        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("acceleration")))).onSuccess(fireball::setAcceleration);
    }
}