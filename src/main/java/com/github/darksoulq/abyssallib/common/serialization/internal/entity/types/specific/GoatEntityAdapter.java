package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Goat;

import java.util.Map;

public class GoatEntityAdapter extends EntityAdapter<Goat> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Goat;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Goat value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("has_left_horn", Codecs.BOOLEAN, value.hasLeftHorn())
            .write("has_right_horn", Codecs.BOOLEAN, value.hasRightHorn())
            .write("is_screaming", Codecs.BOOLEAN, value.isScreaming());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Goat goat)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("has_left_horn", Codecs.BOOLEAN, opt -> opt.ifPresent(goat::setLeftHorn))
            .readOptional("has_right_horn", Codecs.BOOLEAN, opt -> opt.ifPresent(goat::setRightHorn))
            .readOptional("is_screaming", Codecs.BOOLEAN, opt -> opt.ifPresent(goat::setScreaming));

        return ctx.result();
    }
}