package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;

import java.util.Map;

public class PhantomEntityAdapter extends EntityAdapter<Phantom> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Phantom;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Phantom value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("size"), Codecs.INT.encode(ops, value.getSize()));
        map.put(ops.createString("should_burn_in_day"), Codecs.BOOLEAN.encode(ops, value.shouldBurnInDay()));

        if (value.getAnchorLocation() != null) {
            map.put(ops.createString("anchor_location"), Codecs.LOCATION.encode(ops, value.getAnchorLocation()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Phantom phantom)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("size")))).onSuccess(phantom::setSize);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("should_burn_in_day")))).onSuccess(phantom::setShouldBurnInDay);

        D anchorData = map.get(ops.createString("anchor_location"));
        if (anchorData != null) {
            Try.of(() -> Codecs.LOCATION.decode(ops, anchorData)).onSuccess(phantom::setAnchorLocation);
        }
    }
}