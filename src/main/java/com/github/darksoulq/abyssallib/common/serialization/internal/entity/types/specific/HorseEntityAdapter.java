package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;

import java.util.Map;

public class HorseEntityAdapter extends EntityAdapter<Horse> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Horse;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Horse value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("horse_color", Codecs.STRING, value.getColor().name())
            .write("horse_style", Codecs.STRING, value.getStyle().name());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Horse horse)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("horse_color", Codecs.STRING, opt -> opt.ifPresent(colorStr -> {
                try {
                    horse.setColor(Horse.Color.valueOf(colorStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("horse_style", Codecs.STRING, opt -> opt.ifPresent(styleStr -> {
                try {
                    horse.setStyle(Horse.Style.valueOf(styleStr));
                } catch (Exception ignored) {
                }
            }));

        return ctx.result();
    }
}