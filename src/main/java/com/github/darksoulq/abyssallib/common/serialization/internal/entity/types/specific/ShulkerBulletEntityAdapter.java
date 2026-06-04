package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, ShulkerBullet value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("target_delta", Codecs.VECTOR_F, value.getTargetDelta())
            .write("flight_steps", Codecs.INT, value.getFlightSteps());

        if (value.getTarget() != null) {
            ctx.write("target_uuid", Codecs.UUID, value.getTarget().getUniqueId());
        }

        if (value.getCurrentMovementDirection() != null) {
            ctx.write("current_movement_direction", Codecs.STRING, value.getCurrentMovementDirection().name());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof ShulkerBullet bullet)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("target_delta", Codecs.VECTOR_F, opt -> opt.ifPresent(bullet::setTargetDelta))
            .readOptional("flight_steps", Codecs.INT, opt -> opt.ifPresent(bullet::setFlightSteps))
            .readOptional("target_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
                Entity target = Bukkit.getEntity(uuid);
                if (target != null) bullet.setTarget(target);
            }))
            .readOptional("current_movement_direction", Codecs.STRING, opt -> opt.ifPresent(dir -> {
                try {
                    bullet.setCurrentMovementDirection(BlockFace.valueOf(dir));
                } catch (Exception ignored) {
                }
            }));

        return ctx.result();
    }
}