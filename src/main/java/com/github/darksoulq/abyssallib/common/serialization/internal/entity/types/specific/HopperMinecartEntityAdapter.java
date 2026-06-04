package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.HopperMinecart;

import java.util.Map;

public class HopperMinecartEntityAdapter extends EntityAdapter<HopperMinecart> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof HopperMinecart;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, HopperMinecart value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_enabled", Codecs.BOOLEAN, value.isEnabled());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof HopperMinecart minecart)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_enabled", Codecs.BOOLEAN, opt -> opt.ifPresent(minecart::setEnabled));

        return ctx.result();
    }
}