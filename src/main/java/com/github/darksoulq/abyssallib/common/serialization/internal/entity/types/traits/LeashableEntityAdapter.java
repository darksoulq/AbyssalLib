package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import io.papermc.paper.entity.Leashable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.Map;

public class LeashableEntityAdapter extends EntityAdapter<Leashable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Leashable;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Leashable value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        if (value.isLeashed()) {
            value.getLeashHolder();
            ctx.write("leash_holder_uuid", Codecs.UUID, value.getLeashHolder().getUniqueId());
        }
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Leashable leashable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("leash_holder_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
            Entity holder = Bukkit.getEntity(uuid);
            if (holder != null) leashable.setLeashHolder(holder);
        }));

        return ctx.result();
    }
}