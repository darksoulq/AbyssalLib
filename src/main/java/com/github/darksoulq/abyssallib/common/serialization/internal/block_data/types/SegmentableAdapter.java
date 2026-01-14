package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Segmentable;

public class SegmentableAdapter extends Adapter<Segmentable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Segmentable;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Segmentable value) throws Codec.CodecException {
        return Codecs.INT.encode(ops, value.getSegmentAmount());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof Segmentable segmentable)) return;
        int value = Codecs.INT.decode(ops, input);
        if (value < segmentable.getMinimumSegmentAmount() || value > segmentable.getMaximumSegmentAmount()) return;
        segmentable.setSegmentAmount(value);
    }
}
