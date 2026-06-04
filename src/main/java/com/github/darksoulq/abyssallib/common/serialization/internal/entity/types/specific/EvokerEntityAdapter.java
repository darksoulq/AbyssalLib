package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Sheep;

import java.util.Map;

public class EvokerEntityAdapter extends EntityAdapter<Evoker> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Evoker;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Evoker value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        if (value.getWololoTarget() != null) {
            ctx.write("wololo_target_uuid", Codecs.UUID, value.getWololoTarget().getUniqueId());
        }
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Evoker evoker)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("wololo_target_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
            Entity target = Bukkit.getEntity(uuid);
            if (target instanceof Sheep sheep) {
                evoker.setWololoTarget(sheep);
            }
        }));

        return ctx.result();
    }
}