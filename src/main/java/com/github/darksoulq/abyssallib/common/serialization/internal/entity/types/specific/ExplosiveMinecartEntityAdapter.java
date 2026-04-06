package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.ExplosiveMinecart;

import java.util.Map;

public class ExplosiveMinecartEntityAdapter extends EntityAdapter<ExplosiveMinecart> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ExplosiveMinecart;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, ExplosiveMinecart value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("fuse_ticks"), Codecs.INT.encode(ops, value.getFuseTicks()));
        map.put(ops.createString("explosion_speed_factor"), Codecs.FLOAT.encode(ops, value.getExplosionSpeedFactor()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof ExplosiveMinecart minecart)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("fuse_ticks")))).onSuccess(minecart::setFuseTicks);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("explosion_speed_factor")))).onSuccess(minecart::setExplosionSpeedFactor);
    }
}