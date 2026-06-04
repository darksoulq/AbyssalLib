package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Trident;

import java.util.Map;

public class TridentEntityAdapter extends EntityAdapter<Trident> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Trident;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Trident value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("has_glint", Codecs.BOOLEAN, value.hasGlint())
            .write("loyalty_level", Codecs.INT, value.getLoyaltyLevel())
            .write("has_dealt_damage", Codecs.BOOLEAN, value.hasDealtDamage())
            .write("damage", Codecs.DOUBLE, value.getDamage());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Trident trident)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("has_glint", Codecs.BOOLEAN, opt -> opt.ifPresent(trident::setGlint))
            .readOptional("loyalty_level", Codecs.INT, opt -> opt.ifPresent(trident::setLoyaltyLevel))
            .readOptional("has_dealt_damage", Codecs.BOOLEAN, opt -> opt.ifPresent(trident::setHasDealtDamage))
            .readOptional("damage", Codecs.DOUBLE, opt -> opt.ifPresent(trident::setDamage));

        return ctx.result();
    }
}