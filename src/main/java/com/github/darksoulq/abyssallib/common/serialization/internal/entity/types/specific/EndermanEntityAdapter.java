package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Map;

public class EndermanEntityAdapter extends EntityAdapter<Enderman> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Enderman;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Enderman value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_screaming"), Codecs.BOOLEAN.encode(ops, value.isScreaming()));
        map.put(ops.createString("has_been_stared_at"), Codecs.BOOLEAN.encode(ops, value.hasBeenStaredAt()));

        if (value.getCarriedBlock() != null) {
            BlockInfo info = new BlockInfo(new Vector(0,0,0), value.getCarriedBlock(), null, null, null);
            map.put(ops.createString("carried_block"), ExtraCodecs.BLOCK_INFO.encode(ops, info));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Enderman enderman)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_screaming")))).onSuccess(enderman::setScreaming);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_been_stared_at")))).onSuccess(enderman::setHasBeenStaredAt);

        D blockData = map.get(ops.createString("carried_block"));
        if (blockData != null) {
            Try.of(() -> ExtraCodecs.BLOCK_INFO.decode(ops, blockData)).onSuccess(info -> {
                if (info.block() instanceof BlockData data) {
                    enderman.setCarriedBlock(data);
                }
            });
        }
    }
}