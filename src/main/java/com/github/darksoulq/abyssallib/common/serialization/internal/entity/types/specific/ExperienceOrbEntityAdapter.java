package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;

import java.util.Map;

public class ExperienceOrbEntityAdapter extends EntityAdapter<ExperienceOrb> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ExperienceOrb;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, ExperienceOrb value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("experience", Codecs.INT, value.getExperience())
            .write("count", Codecs.INT, value.getCount());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof ExperienceOrb orb)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("experience", Codecs.INT, opt -> opt.ifPresent(orb::setExperience))
            .readOptional("count", Codecs.INT, opt -> opt.ifPresent(orb::setCount));

        return ctx.result();
    }
}