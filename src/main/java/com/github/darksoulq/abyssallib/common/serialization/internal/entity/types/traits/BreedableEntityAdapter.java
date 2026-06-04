package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Entity;

import java.util.Map;

public class BreedableEntityAdapter extends EntityAdapter<Breedable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Breedable;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Breedable value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("age_locked", Codecs.BOOLEAN, value.getAgeLock())
            .write("can_breed", Codecs.BOOLEAN, value.canBreed());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Breedable breedable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("age_locked", Codecs.BOOLEAN, opt -> opt.ifPresent(breedable::setAgeLock))
            .readOptional("can_breed", Codecs.BOOLEAN, opt -> opt.ifPresent(breedable::setBreed));

        return ctx.result();
    }
}