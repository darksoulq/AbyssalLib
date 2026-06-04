package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;

import java.util.Map;

public class BatEntityAdapter extends EntityAdapter<Bat> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Bat;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Bat value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("is_awake", Codecs.BOOLEAN, value.isAwake())
            .writeNullable("target_location", Codecs.LOCATION, value.getTargetLocation());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Bat bat)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_awake", Codecs.BOOLEAN, opt -> opt.ifPresent(bat::setAwake))
            .readOptional("target_location", Codecs.LOCATION, opt -> opt.ifPresent(bat::setTargetLocation));

        return ctx.result();
    }
}