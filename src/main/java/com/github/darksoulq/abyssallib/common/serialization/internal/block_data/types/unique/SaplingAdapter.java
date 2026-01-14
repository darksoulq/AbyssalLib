package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;

public class SaplingAdapter extends Adapter<Sapling> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Sapling;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Sapling value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getStage());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Sapling sapling)) return;
        int value = Codecs.INT.decode(ops, input);
        if (value > sapling.getMaximumStage()) return;
        sapling.setStage(value);
    }
}
