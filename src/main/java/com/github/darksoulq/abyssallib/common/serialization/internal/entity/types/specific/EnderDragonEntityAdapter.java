package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;

import java.util.Map;

public class EnderDragonEntityAdapter extends EntityAdapter<EnderDragon> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof EnderDragon;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, EnderDragon value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("phase"), Codecs.STRING.encode(ops, value.getPhase().name()));
        map.put(ops.createString("podium"), Codecs.LOCATION.encode(ops, value.getPodium()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof EnderDragon dragon)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("phase")))).onSuccess(s -> dragon.setPhase(EnderDragon.Phase.valueOf(s)));
        Try.of(() -> Codecs.LOCATION.decode(ops, map.get(ops.createString("podium")))).onSuccess(dragon::setPodium);
    }
}