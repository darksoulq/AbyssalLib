package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;

import java.util.Map;

public class ProjectileEntityAdapter extends EntityAdapter<Projectile> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Projectile;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Projectile value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("has_left_shooter", Codecs.BOOLEAN, value.hasLeftShooter())
            .write("has_been_shot", Codecs.BOOLEAN, value.hasBeenShot())
            .writeNullable("owner_uuid", Codecs.UUID, value.getOwnerUniqueId());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Projectile projectile)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("has_left_shooter", Codecs.BOOLEAN, opt -> opt.ifPresent(projectile::setHasLeftShooter))
            .readOptional("has_been_shot", Codecs.BOOLEAN, opt -> opt.ifPresent(projectile::setHasBeenShot))
            .readOptional("owner_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
                Entity shooter = Bukkit.getEntity(uuid);
                if (shooter instanceof org.bukkit.projectiles.ProjectileSource source) {
                    projectile.setShooter(source);
                }
            }));

        return ctx.result();
    }
}