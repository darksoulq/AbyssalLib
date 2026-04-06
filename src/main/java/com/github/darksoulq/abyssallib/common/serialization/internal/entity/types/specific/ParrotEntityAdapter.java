package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;

import java.util.Map;

public class ParrotEntityAdapter extends EntityAdapter<Parrot> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Parrot;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Parrot value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("parrot_variant"), Codecs.STRING.encode(ops, value.getVariant().name()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Parrot parrot)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("parrot_variant")))).onSuccess(s -> parrot.setVariant(Parrot.Variant.valueOf(s)));
    }
}