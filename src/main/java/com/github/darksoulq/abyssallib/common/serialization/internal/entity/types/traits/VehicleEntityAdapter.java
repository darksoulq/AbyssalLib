package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;

import java.util.Map;

public class VehicleEntityAdapter extends EntityAdapter<Vehicle> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Vehicle;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Vehicle value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("velocity"), Codecs.VECTOR_F.encode(ops, value.getVelocity()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Vehicle vehicle)) return;

        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("velocity")))).onSuccess(vehicle::setVelocity);
    }
}