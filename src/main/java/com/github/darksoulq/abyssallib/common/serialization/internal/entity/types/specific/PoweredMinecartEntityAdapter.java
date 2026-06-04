package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.PoweredMinecart;

import java.util.Map;

public class PoweredMinecartEntityAdapter extends EntityAdapter<PoweredMinecart> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof PoweredMinecart;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, PoweredMinecart value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("fuel", Codecs.INT, value.getFuel())
            .write("push_x", Codecs.DOUBLE, value.getPushX())
            .write("push_z", Codecs.DOUBLE, value.getPushZ());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof PoweredMinecart minecart)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("fuel", Codecs.INT, opt -> opt.ifPresent(minecart::setFuel))
            .readOptional("push_x", Codecs.DOUBLE, opt -> opt.ifPresent(minecart::setPushX))
            .readOptional("push_z", Codecs.DOUBLE, opt -> opt.ifPresent(minecart::setPushZ));

        return ctx.result();
    }
}