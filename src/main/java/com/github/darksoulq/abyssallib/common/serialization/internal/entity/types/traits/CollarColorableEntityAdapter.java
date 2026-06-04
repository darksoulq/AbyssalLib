package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import io.papermc.paper.entity.CollarColorable;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;

import java.util.Map;

public class CollarColorableEntityAdapter extends EntityAdapter<CollarColorable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof CollarColorable;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, CollarColorable value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        if (value.getCollarColor() != null) {
            ctx.write("collar_color", Codecs.STRING, value.getCollarColor().name());
        }
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof CollarColorable colorable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("collar_color", Codecs.STRING, opt -> opt.ifPresent(colorStr -> {
            try {
                colorable.setCollarColor(DyeColor.valueOf(colorStr));
            } catch (Exception ignored) {
            }
        }));

        return ctx.result();
    }
}