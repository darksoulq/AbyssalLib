package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Brushable;

public class BrushableAdapter extends Adapter<Brushable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Brushable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Brushable value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getDusted());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Brushable brushable)) return;
        int value = Codecs.INT.decode(ops, input);
        if (value > brushable.getMaximumDusted()) return;
        brushable.setDusted(value);
    }
}
