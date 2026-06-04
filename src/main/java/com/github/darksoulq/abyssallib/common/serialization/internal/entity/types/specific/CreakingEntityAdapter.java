package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.EncodeContext;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Creaking;
import org.bukkit.entity.Entity;

import java.util.Map;

public class CreakingEntityAdapter extends EntityAdapter<Creaking> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Creaking;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Creaking value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.writeNullable("home", Codecs.LOCATION, value.getHome());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        return DataResult.success(null);
    }
}