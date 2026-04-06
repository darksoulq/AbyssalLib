package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Map;

public class BlockDisplayEntityAdapter extends EntityAdapter<BlockDisplay> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof BlockDisplay;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, BlockDisplay value, Map<D, D> map) throws Codec.CodecException {
        BlockInfo info = new BlockInfo(new Vector(0, 0, 0), value.getBlock(), null, null, null);
        map.put(ops.createString("block_info"), ExtraCodecs.BLOCK_INFO.encode(ops, info));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof BlockDisplay display)) return;

        Try.of(() -> ExtraCodecs.BLOCK_INFO.decode(ops, map.get(ops.createString("block_info")))).onSuccess(info -> {
            if (info.block() instanceof BlockData bd) {
                display.setBlock(bd);
            }
        });
    }
}