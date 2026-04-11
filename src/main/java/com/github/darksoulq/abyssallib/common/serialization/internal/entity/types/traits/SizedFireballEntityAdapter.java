package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SizedFireball;

import java.util.Map;

public class SizedFireballEntityAdapter extends EntityAdapter<SizedFireball> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof SizedFireball;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, SizedFireball value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("display_item"), Codecs.ITEM_STACK.encode(ops, value.getDisplayItem()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof SizedFireball fireball)) return;

        Try.of(() -> Codecs.ITEM_STACK.decode(ops, map.get(ops.createString("display_item")))).onSuccess(fireball::setDisplayItem);
    }
}