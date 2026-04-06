package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;

import java.util.Map;

public class ExplosiveEntityAdapter extends EntityAdapter<Explosive> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Explosive;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Explosive value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("yield"), Codecs.FLOAT.encode(ops, value.getYield()));
        map.put(ops.createString("is_incendiary"), Codecs.BOOLEAN.encode(ops, value.isIncendiary()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Explosive explosive)) return;

        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("yield")))).onSuccess(explosive::setYield);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_incendiary")))).onSuccess(explosive::setIsIncendiary);
    }
}