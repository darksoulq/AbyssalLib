package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ThrowableProjectile;

import java.util.Map;

public class ThrowableProjectileEntityAdapter extends EntityAdapter<ThrowableProjectile> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ThrowableProjectile;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, ThrowableProjectile value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("item", Codecs.ITEM_STACK, value.getItem());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof ThrowableProjectile projectile)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("item", Codecs.ITEM_STACK, opt -> opt.ifPresent(projectile::setItem));

        return ctx.result();
    }
}