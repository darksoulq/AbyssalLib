package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
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
    public <D> void serialize(DynamicOps<D> ops, Projectile value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("has_left_shooter"), Codecs.BOOLEAN.encode(ops, value.hasLeftShooter()));
        map.put(ops.createString("has_been_shot"), Codecs.BOOLEAN.encode(ops, value.hasBeenShot()));

        if (value.getOwnerUniqueId() != null) {
            map.put(ops.createString("owner_uuid"), Codecs.UUID.encode(ops, value.getOwnerUniqueId()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Projectile projectile)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_left_shooter")))).onSuccess(projectile::setHasLeftShooter);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_been_shot")))).onSuccess(projectile::setHasBeenShot);

        D ownerData = map.get(ops.createString("owner_uuid"));
        if (ownerData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, ownerData)).onSuccess(uuid -> {
                Entity shooter = Bukkit.getEntity(uuid);
                if (shooter instanceof org.bukkit.projectiles.ProjectileSource source) {
                    projectile.setShooter(source);
                }
            });
        }
    }
}