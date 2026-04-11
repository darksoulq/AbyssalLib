package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;

import java.util.Map;

public class MinecartEntityAdapter extends EntityAdapter<Minecart> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Minecart;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Minecart value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("damage"), Codecs.DOUBLE.encode(ops, value.getDamage()));
        map.put(ops.createString("max_speed"), Codecs.DOUBLE.encode(ops, value.getMaxSpeed()));
        map.put(ops.createString("is_slow_when_empty"), Codecs.BOOLEAN.encode(ops, value.isSlowWhenEmpty()));
        map.put(ops.createString("display_block_offset"), Codecs.INT.encode(ops, value.getDisplayBlockOffset()));

        map.put(ops.createString("flying_velocity_mod"), Codecs.VECTOR_F.encode(ops, value.getFlyingVelocityMod()));
        map.put(ops.createString("derailed_velocity_mod"), Codecs.VECTOR_F.encode(ops, value.getDerailedVelocityMod()));

        BlockInfo info = new BlockInfo(new Vector(0, 0, 0), value.getDisplayBlockData(), null, null, null);
        map.put(ops.createString("display_block_info"), ExtraCodecs.BLOCK_INFO.encode(ops, info));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Minecart minecart)) return;

        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("damage")))).onSuccess(minecart::setDamage);
        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("max_speed")))).onSuccess(minecart::setMaxSpeed);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_slow_when_empty")))).onSuccess(minecart::setSlowWhenEmpty);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("display_block_offset")))).onSuccess(minecart::setDisplayBlockOffset);

        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("flying_velocity_mod")))).onSuccess(minecart::setFlyingVelocityMod);
        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("derailed_velocity_mod")))).onSuccess(minecart::setDerailedVelocityMod);

        Try.of(() -> ExtraCodecs.BLOCK_INFO.decode(ops, map.get(ops.createString("display_block_info")))).onSuccess(info -> {
            if (info.block() instanceof BlockData bd) {
                minecart.setDisplayBlockData(bd);
            }
        });
    }
}