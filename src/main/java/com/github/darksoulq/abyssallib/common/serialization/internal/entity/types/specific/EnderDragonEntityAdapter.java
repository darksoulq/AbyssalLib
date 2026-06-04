package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;

import java.util.Map;

public class EnderDragonEntityAdapter extends EntityAdapter<EnderDragon> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof EnderDragon;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, EnderDragon value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("phase", Codecs.STRING, value.getPhase().name())
            .write("podium", Codecs.LOCATION, value.getPodium());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof EnderDragon dragon)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("phase", Codecs.STRING, opt -> opt.ifPresent(phase -> {
                try {
                    dragon.setPhase(EnderDragon.Phase.valueOf(phase));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("podium", Codecs.LOCATION, opt -> opt.ifPresent(dragon::setPodium));

        return ctx.result();
    }
}