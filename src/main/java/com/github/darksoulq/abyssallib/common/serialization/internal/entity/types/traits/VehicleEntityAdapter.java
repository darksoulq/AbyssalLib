package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;

import java.util.Map;

public class VehicleEntityAdapter extends EntityAdapter<Vehicle> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Vehicle;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Vehicle value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("velocity", Codecs.VECTOR_F, value.getVelocity());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Vehicle vehicle)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("velocity", Codecs.VECTOR_F, opt -> opt.ifPresent(vehicle::setVelocity));

        return ctx.result();
    }
}