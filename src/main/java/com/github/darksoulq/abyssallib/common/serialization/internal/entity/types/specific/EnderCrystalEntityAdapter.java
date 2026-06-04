package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;

import java.util.Map;

public class EnderCrystalEntityAdapter extends EntityAdapter<EnderCrystal> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof EnderCrystal;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, EnderCrystal value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("is_showing_bottom", Codecs.BOOLEAN, value.isShowingBottom())
            .writeNullable("beam_target", Codecs.LOCATION, value.getBeamTarget());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof EnderCrystal crystal)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_showing_bottom", Codecs.BOOLEAN, opt -> opt.ifPresent(crystal::setShowingBottom))
            .readOptional("beam_target", Codecs.LOCATION, opt -> opt.ifPresent(crystal::setBeamTarget));

        return ctx.result();
    }
}