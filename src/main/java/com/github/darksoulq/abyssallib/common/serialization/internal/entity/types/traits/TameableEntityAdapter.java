package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;

import java.util.Map;

public class TameableEntityAdapter extends EntityAdapter<Tameable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Tameable;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Tameable value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("is_tamed", Codecs.BOOLEAN, value.isTamed())
            .writeNullable("owner_uuid", Codecs.UUID, value.getOwnerUniqueId());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Tameable tameable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_tamed", Codecs.BOOLEAN, opt -> opt.ifPresent(tameable::setTamed))
            .readOptional("owner_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> tameable.setOwner(Bukkit.getOfflinePlayer(uuid))));

        return ctx.result();
    }
}