package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Goat;

import java.util.Map;

public class GoatEntityAdapter extends EntityAdapter<Goat> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Goat;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Goat value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("has_left_horn"), Codecs.BOOLEAN.encode(ops, value.hasLeftHorn()));
        map.put(ops.createString("has_right_horn"), Codecs.BOOLEAN.encode(ops, value.hasRightHorn()));
        map.put(ops.createString("is_screaming"), Codecs.BOOLEAN.encode(ops, value.isScreaming()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Goat goat)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_left_horn")))).onSuccess(goat::setLeftHorn);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_right_horn")))).onSuccess(goat::setRightHorn);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_screaming")))).onSuccess(goat::setScreaming);
    }
}