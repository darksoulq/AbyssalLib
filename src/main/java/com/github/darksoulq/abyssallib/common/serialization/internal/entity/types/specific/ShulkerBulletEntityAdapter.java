package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ShulkerBullet;

import java.util.Map;

public class ShulkerBulletEntityAdapter extends EntityAdapter<ShulkerBullet> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ShulkerBullet;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, ShulkerBullet value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("target_delta"), Codecs.VECTOR_F.encode(ops, value.getTargetDelta()));
        map.put(ops.createString("flight_steps"), Codecs.INT.encode(ops, value.getFlightSteps()));

        if (value.getTarget() != null) {
            map.put(ops.createString("target_uuid"), Codecs.UUID.encode(ops, value.getTarget().getUniqueId()));
        }

        if (value.getCurrentMovementDirection() != null) {
            map.put(ops.createString("current_movement_direction"), Codecs.STRING.encode(ops, value.getCurrentMovementDirection().name()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof ShulkerBullet bullet)) return;

        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("target_delta")))).onSuccess(bullet::setTargetDelta);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("flight_steps")))).onSuccess(bullet::setFlightSteps);

        D targetData = map.get(ops.createString("target_uuid"));
        if (targetData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, targetData)).onSuccess(uuid -> {
                Entity target = Bukkit.getEntity(uuid);
                if (target != null) bullet.setTarget(target);
            });
        }

        D directionData = map.get(ops.createString("current_movement_direction"));
        if (directionData != null) {
            Try.of(() -> Codecs.STRING.decode(ops, directionData)).onSuccess(s -> bullet.setCurrentMovementDirection(BlockFace.valueOf(s)));
        }
    }
}