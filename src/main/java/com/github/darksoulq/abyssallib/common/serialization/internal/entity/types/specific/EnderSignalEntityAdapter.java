package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;

import java.util.Map;

public class EnderSignalEntityAdapter extends EntityAdapter<EnderSignal> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof EnderSignal;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, EnderSignal value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("drop_item"), Codecs.BOOLEAN.encode(ops, value.getDropItem()));
        map.put(ops.createString("despawn_timer"), Codecs.INT.encode(ops, value.getDespawnTimer()));
        map.put(ops.createString("item"), Codecs.ITEM_STACK.encode(ops, value.getItem()));

        if (value.getTargetLocation() != null) {
            map.put(ops.createString("target_location"), Codecs.LOCATION.encode(ops, value.getTargetLocation()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof EnderSignal signal)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("drop_item")))).onSuccess(signal::setDropItem);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("despawn_timer")))).onSuccess(signal::setDespawnTimer);
        Try.of(() -> Codecs.ITEM_STACK.decode(ops, map.get(ops.createString("item")))).onSuccess(signal::setItem);

        D locData = map.get(ops.createString("target_location"));
        if (locData != null) {
            Try.of(() -> Codecs.LOCATION.decode(ops, locData)).onSuccess(loc -> signal.setTargetLocation(loc, false));
        }
    }
}