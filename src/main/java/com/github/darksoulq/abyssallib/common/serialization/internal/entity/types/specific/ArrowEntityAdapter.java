package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionType;

import java.util.Map;

public class ArrowEntityAdapter extends EntityAdapter<Arrow> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Arrow;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Arrow value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        if (value.getBasePotionType() != null) {
            ctx.write("base_potion_type", Codecs.STRING, value.getBasePotionType().name());
        }

        ctx.writeNullable("color", Codecs.COLOR, value.getColor());

        if (value.hasCustomEffects()) {
            ctx.write("custom_effects", ExtraCodecs.POTION_EFFECT.list(), value.getCustomEffects());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Arrow arrow)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("base_potion_type", Codecs.STRING, opt -> opt.ifPresent(typeStr -> {
                try {
                    arrow.setBasePotionType(PotionType.valueOf(typeStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("color", Codecs.COLOR, opt -> opt.ifPresent(arrow::setColor))
            .readOptional("custom_effects", ExtraCodecs.POTION_EFFECT.list(), opt -> opt.ifPresent(effects -> {
                effects.forEach(effect -> arrow.addCustomEffect(effect, true));
            }));

        return ctx.result();
    }
}