package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Vex;

import java.util.Map;

public class VexEntityAdapter extends EntityAdapter<Vex> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Vex;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Vex value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("is_charging", Codecs.BOOLEAN, value.isCharging())
            .write("has_limited_lifetime", Codecs.BOOLEAN, value.hasLimitedLifetime())
            .write("limited_lifetime_ticks", Codecs.INT, value.getLimitedLifetimeTicks())
            .writeNullable("bound_location", Codecs.LOCATION, value.getBound());

        if (value.getSummoner() != null) {
            ctx.write("summoner_uuid", Codecs.UUID, value.getSummoner().getUniqueId());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Vex vex)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_charging", Codecs.BOOLEAN, opt -> opt.ifPresent(vex::setCharging))
            .readOptional("has_limited_lifetime", Codecs.BOOLEAN, opt -> opt.ifPresent(vex::setLimitedLifetime))
            .readOptional("limited_lifetime_ticks", Codecs.INT, opt -> opt.ifPresent(vex::setLimitedLifetimeTicks))
            .readOptional("bound_location", Codecs.LOCATION, opt -> opt.ifPresent(vex::setBound))
            .readOptional("summoner_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
                Entity summoner = Bukkit.getEntity(uuid);
                if (summoner instanceof Mob mob) vex.setSummoner(mob);
            }));

        return ctx.result();
    }
}