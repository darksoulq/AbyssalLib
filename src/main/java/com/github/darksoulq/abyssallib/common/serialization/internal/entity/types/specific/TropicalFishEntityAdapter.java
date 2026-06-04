package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TropicalFish;

import java.util.Map;

public class TropicalFishEntityAdapter extends EntityAdapter<TropicalFish> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof TropicalFish;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, TropicalFish value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("pattern", Codecs.STRING, value.getPattern().name())
            .write("body_color", Codecs.STRING, value.getBodyColor().name())
            .write("pattern_color", Codecs.STRING, value.getPatternColor().name());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof TropicalFish fish)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("pattern", Codecs.STRING, opt -> opt.ifPresent(str -> {
                try {
                    fish.setPattern(TropicalFish.Pattern.valueOf(str));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("body_color", Codecs.STRING, opt -> opt.ifPresent(str -> {
                try {
                    fish.setBodyColor(DyeColor.valueOf(str));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("pattern_color", Codecs.STRING, opt -> opt.ifPresent(str -> {
                try {
                    fish.setPatternColor(DyeColor.valueOf(str));
                } catch (Exception ignored) {
                }
            }));

        return ctx.result();
    }
}