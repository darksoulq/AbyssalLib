package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;

import java.util.Map;

public class MushroomCowEntityAdapter extends EntityAdapter<MushroomCow> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof MushroomCow;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, MushroomCow value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("variant", Codecs.STRING, value.getVariant().name());

        if (!value.getStewEffects().isEmpty()) {
            ctx.write("stew_effects", ExtraCodecs.SUSPICIOUS_EFFECT_ENTRY.list(), value.getStewEffects());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof MushroomCow mooshroom)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("variant", Codecs.STRING, opt -> opt.ifPresent(varStr -> {
                try {
                    mooshroom.setVariant(MushroomCow.Variant.valueOf(varStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("stew_effects", ExtraCodecs.SUSPICIOUS_EFFECT_ENTRY.list(), opt -> opt.ifPresent(mooshroom::setStewEffects));

        return ctx.result();
    }
}