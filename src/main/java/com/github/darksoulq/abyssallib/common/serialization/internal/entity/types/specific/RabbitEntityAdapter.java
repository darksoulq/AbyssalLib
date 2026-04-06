package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;

import java.util.Map;

public class RabbitEntityAdapter extends EntityAdapter<Rabbit> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Rabbit;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Rabbit value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("rabbit_type"), Codecs.STRING.encode(ops, value.getRabbitType().name()));
        map.put(ops.createString("more_carrot_ticks"), Codecs.INT.encode(ops, value.getMoreCarrotTicks()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Rabbit rabbit)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("rabbit_type")))).onSuccess(s -> rabbit.setRabbitType(Rabbit.Type.valueOf(s)));
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("more_carrot_ticks")))).onSuccess(rabbit::setMoreCarrotTicks);
    }
}