package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Shulker;

import java.util.Map;

public class ShulkerEntityAdapter extends EntityAdapter<Shulker> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Shulker;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Shulker value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("peek"), Codecs.FLOAT.encode(ops, value.getPeek()));
        map.put(ops.createString("attached_face"), Codecs.STRING.encode(ops, value.getAttachedFace().name()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Shulker shulker)) return;

        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("peek")))).onSuccess(shulker::setPeek);
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("attached_face")))).onSuccess(s -> shulker.setAttachedFace(BlockFace.valueOf(s)));
    }
}