package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.PoweredMinecart;

import java.util.Map;

public class PoweredMinecartEntityAdapter extends EntityAdapter<PoweredMinecart> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof PoweredMinecart;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, PoweredMinecart value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("fuel"), Codecs.INT.encode(ops, value.getFuel()));
        map.put(ops.createString("push_x"), Codecs.DOUBLE.encode(ops, value.getPushX()));
        map.put(ops.createString("push_z"), Codecs.DOUBLE.encode(ops, value.getPushZ()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof PoweredMinecart minecart)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("fuel")))).onSuccess(minecart::setFuel);
        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("push_x")))).onSuccess(minecart::setPushX);
        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("push_z")))).onSuccess(minecart::setPushZ);
    }
}