package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AnimalsEntityAdapter extends EntityAdapter<Animals> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Animals;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Animals value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.writeNullable("breed_cause", Codecs.UUID, value.getBreedCause())
            .write("love_mode_ticks", Codecs.INT, value.getLoveModeTicks());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Animals animals)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("breed_cause", Codecs.UUID, opt -> opt.ifPresent(animals::setBreedCause))
            .readOptional("love_mode_ticks", Codecs.INT, opt -> opt.ifPresent(animals::setLoveModeTicks));

        return ctx.result();
    }
}