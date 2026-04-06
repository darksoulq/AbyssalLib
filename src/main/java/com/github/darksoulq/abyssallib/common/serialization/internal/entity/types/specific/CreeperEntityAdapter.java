package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;

import java.util.Map;

public class CreeperEntityAdapter extends EntityAdapter<Creeper> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Creeper;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Creeper value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("powered"), Codecs.BOOLEAN.encode(ops, value.isPowered()));
        map.put(ops.createString("ignited"), Codecs.BOOLEAN.encode(ops, value.isIgnited()));
        map.put(ops.createString("max_fuse_ticks"), Codecs.INT.encode(ops, value.getMaxFuseTicks()));
        map.put(ops.createString("fuse_ticks"), Codecs.INT.encode(ops, value.getFuseTicks()));
        map.put(ops.createString("explosion_radius"), Codecs.INT.encode(ops, value.getExplosionRadius()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Creeper creeper)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("powered")))).onSuccess(creeper::setPowered);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("ignited")))).onSuccess(creeper::setIgnited);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("max_fuse_ticks")))).onSuccess(creeper::setMaxFuseTicks);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("fuse_ticks")))).onSuccess(creeper::setFuseTicks);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("explosion_radius")))).onSuccess(creeper::setExplosionRadius);
    }
}