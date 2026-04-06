package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sniffer;

import java.util.ArrayList;
import java.util.Map;

public class SnifferEntityAdapter extends EntityAdapter<Sniffer> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Sniffer;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Sniffer value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("state"), Codecs.STRING.encode(ops, value.getState().name()));

        if (!value.getExploredLocations().isEmpty()) {
            map.put(ops.createString("explored_locations"), Codecs.LOCATION.list().encode(ops, new ArrayList<>(value.getExploredLocations())));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Sniffer sniffer)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("state")))).onSuccess(s -> sniffer.setState(Sniffer.State.valueOf(s)));

        D locsData = map.get(ops.createString("explored_locations"));
        if (locsData != null) {
            Try.of(() -> Codecs.LOCATION.list().decode(ops, locsData)).onSuccess(locs -> {
                locs.forEach(sniffer::addExploredLocation);
            });
        }
    }
}