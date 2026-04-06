package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ravager;

import java.util.Map;

public class RavagerEntityAdapter extends EntityAdapter<Ravager> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Ravager;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Ravager value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("attack_ticks"), Codecs.INT.encode(ops, value.getAttackTicks()));
        map.put(ops.createString("stunned_ticks"), Codecs.INT.encode(ops, value.getStunnedTicks()));
        map.put(ops.createString("roar_ticks"), Codecs.INT.encode(ops, value.getRoarTicks()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Ravager ravager)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("attack_ticks")))).onSuccess(ravager::setAttackTicks);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("stunned_ticks")))).onSuccess(ravager::setStunnedTicks);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("roar_ticks")))).onSuccess(ravager::setRoarTicks);
    }
}