package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ThrownPotion;

import java.util.Map;

public class ThrownPotionEntityAdapter extends EntityAdapter<ThrownPotion> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ThrownPotion;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, ThrownPotion value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("item"), Codecs.ITEM_STACK.encode(ops, value.getItem()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof ThrownPotion potion)) return;

        Try.of(() -> Codecs.ITEM_STACK.decode(ops, map.get(ops.createString("item")))).onSuccess(potion::setItem);
    }
}