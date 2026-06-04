package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class EvokerFangsEntityAdapter extends EntityAdapter<EvokerFangs> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof EvokerFangs;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, EvokerFangs value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        if (value.getOwner() != null) {
            ctx.write("owner_uuid", Codecs.UUID, value.getOwner().getUniqueId());
        }
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof EvokerFangs fangs)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("owner_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
            Entity owner = Bukkit.getEntity(uuid);
            if (owner instanceof LivingEntity livingOwner) {
                fangs.setOwner(livingOwner);
            }
        }));

        return ctx.result();
    }
}