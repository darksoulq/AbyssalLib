package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;

import java.util.Map;

public class OcelotEntityAdapter extends EntityAdapter<Ocelot> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Ocelot;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Ocelot value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_trusting"), Codecs.BOOLEAN.encode(ops, value.isTrusting()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Ocelot ocelot)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_trusting")))).onSuccess(ocelot::setTrusting);
    }
}