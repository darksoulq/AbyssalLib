package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.entity.Bucketable;
import org.bukkit.entity.Entity;

import java.util.Map;

public class BucketableEntityAdapter extends EntityAdapter<Bucketable> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Bucketable;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Bucketable value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_from_bucket"), Codecs.BOOLEAN.encode(ops, value.isFromBucket()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Bucketable bucketable)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_from_bucket")))).onSuccess(bucketable::setFromBucket);
    }
}