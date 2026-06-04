package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import io.papermc.paper.entity.Bucketable;
import org.bukkit.entity.Entity;

import java.util.Map;

public class BucketableEntityAdapter extends EntityAdapter<Bucketable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Bucketable;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Bucketable value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("is_from_bucket", Codecs.BOOLEAN, value.isFromBucket());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Bucketable bucketable)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_from_bucket", Codecs.BOOLEAN, opt -> opt.ifPresent(bucketable::setFromBucket));

        return ctx.result();
    }
}