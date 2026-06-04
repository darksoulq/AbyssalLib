package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.material.Colorable;

import java.util.Map;

public class ColorableEntityAdapter extends EntityAdapter<Colorable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Colorable;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Colorable value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        if (value.getColor() != null) {
            ctx.write("color", Codecs.STRING, value.getColor().name());
        }
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Colorable colorable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("color", Codecs.STRING, opt -> opt.ifPresent(colorStr -> {
            try {
                colorable.setColor(DyeColor.valueOf(colorStr));
            } catch (Exception ignored) {
            }
        }));

        return ctx.result();
    }
}