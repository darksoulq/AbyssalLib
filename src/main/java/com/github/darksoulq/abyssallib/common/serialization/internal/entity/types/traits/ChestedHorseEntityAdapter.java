package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;

import java.util.Map;

public class ChestedHorseEntityAdapter extends EntityAdapter<ChestedHorse> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ChestedHorse;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, ChestedHorse value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_carrying_chest"), Codecs.BOOLEAN.encode(ops, value.isCarryingChest()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof ChestedHorse horse)) return;
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_carrying_chest")))).onSuccess(horse::setCarryingChest);
    }
}