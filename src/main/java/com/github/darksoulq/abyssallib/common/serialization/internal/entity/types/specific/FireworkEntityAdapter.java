package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Map;

public class FireworkEntityAdapter extends EntityAdapter<Firework> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Firework;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Firework value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        FireworkMeta meta = value.getFireworkMeta();

        ctx.write("is_shot_at_angle", Codecs.BOOLEAN, value.isShotAtAngle())
            .write("power", Codecs.INT, meta.getPower());

        if (meta.hasEffects()) {
            ctx.write("effects", ExtraCodecs.FIREWORK_EFFECT.list(), meta.getEffects());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Firework firework)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_shot_at_angle", Codecs.BOOLEAN, opt -> opt.ifPresent(firework::setShotAtAngle));

        FireworkMeta meta = firework.getFireworkMeta();
        ctx.readOptional("power", Codecs.INT, opt -> opt.ifPresent(meta::setPower))
            .readOptional("effects", ExtraCodecs.FIREWORK_EFFECT.list(), opt -> opt.ifPresent(effects -> {
                effects.forEach(meta::addEffect);
            }));
        firework.setFireworkMeta(meta);

        return ctx.result();
    }
}