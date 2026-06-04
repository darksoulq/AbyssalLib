package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;

import java.util.Map;

public class WitherEntityAdapter extends EntityAdapter<Wither> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Wither;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Wither value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("invulnerable_ticks", Codecs.INT, value.getInvulnerableTicks())
            .write("can_travel_through_portals", Codecs.BOOLEAN, value.canTravelThroughPortals());

        if (value.getTarget(Wither.Head.CENTER) != null) {
            ctx.write("target_center", Codecs.UUID, value.getTarget(Wither.Head.CENTER).getUniqueId());
        }
        if (value.getTarget(Wither.Head.LEFT) != null) {
            ctx.write("target_left", Codecs.UUID, value.getTarget(Wither.Head.LEFT).getUniqueId());
        }
        if (value.getTarget(Wither.Head.RIGHT) != null) {
            ctx.write("target_right", Codecs.UUID, value.getTarget(Wither.Head.RIGHT).getUniqueId());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Wither wither)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("invulnerable_ticks", Codecs.INT, opt -> opt.ifPresent(wither::setInvulnerableTicks))
            .readOptional("can_travel_through_portals", Codecs.BOOLEAN, opt -> opt.ifPresent(wither::setCanTravelThroughPortals))
            .readOptional("target_center", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
                Entity target = Bukkit.getEntity(uuid);
                if (target instanceof LivingEntity livingTarget) wither.setTarget(Wither.Head.CENTER, livingTarget);
            }))
            .readOptional("target_left", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
                Entity target = Bukkit.getEntity(uuid);
                if (target instanceof LivingEntity livingTarget) wither.setTarget(Wither.Head.LEFT, livingTarget);
            }))
            .readOptional("target_right", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
                Entity target = Bukkit.getEntity(uuid);
                if (target instanceof LivingEntity livingTarget) wither.setTarget(Wither.Head.RIGHT, livingTarget);
            }));

        return ctx.result();
    }
}