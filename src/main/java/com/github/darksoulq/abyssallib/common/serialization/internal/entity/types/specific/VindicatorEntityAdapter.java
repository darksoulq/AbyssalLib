package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vindicator;

import java.util.Map;

public class VindicatorEntityAdapter extends EntityAdapter<Vindicator> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Vindicator;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Vindicator value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_johnny"), Codecs.BOOLEAN.encode(ops, value.isJohnny()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Vindicator vindicator)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_johnny")))).onSuccess(vindicator::setJohnny);
    }
}