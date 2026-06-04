package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

import java.util.Map;

public class MobEntityAdapter extends EntityAdapter<Mob> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Mob;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Mob value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("despawn_peaceful_override", Codecs.STRING, value.getDespawnInPeacefulOverride().name())
            .write("aware", Codecs.BOOLEAN, value.isAware())
            .write("aggressive", Codecs.BOOLEAN, value.isAggressive())
            .write("left_handed", Codecs.BOOLEAN, value.isLeftHanded());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Mob mob)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("despawn_peaceful_override", Codecs.STRING, opt -> opt.ifPresent(val -> {
                try {
                    mob.setDespawnInPeacefulOverride(TriState.valueOf(val));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("aware", Codecs.BOOLEAN, opt -> opt.ifPresent(mob::setAware))
            .readOptional("aggressive", Codecs.BOOLEAN, opt -> opt.ifPresent(mob::setAggressive))
            .readOptional("left_handed", Codecs.BOOLEAN, opt -> opt.ifPresent(mob::setLeftHanded));

        return ctx.result();
    }
}