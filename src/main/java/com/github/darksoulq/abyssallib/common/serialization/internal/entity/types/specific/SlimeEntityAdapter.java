package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;

import java.util.Map;

public class SlimeEntityAdapter extends EntityAdapter<Slime> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Slime;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Slime value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("slime_size"), Codecs.INT.encode(ops, value.getSize()));
        map.put(ops.createString("can_wander"), Codecs.BOOLEAN.encode(ops, value.canWander()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Slime slime)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("slime_size")))).onSuccess(slime::setSize);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_wander")))).onSuccess(slime::setWander);
    }
}