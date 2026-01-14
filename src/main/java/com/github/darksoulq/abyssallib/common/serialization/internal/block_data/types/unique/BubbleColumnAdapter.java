package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BubbleColumn;

public class BubbleColumnAdapter extends Adapter<BubbleColumn> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof BubbleColumn;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, BubbleColumn value) throws Codec.CodecException {
        return Codecs.BOOLEAN.encode(ops, value.isDrag());
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException {
        if (!(base instanceof BubbleColumn bubbleColumn)) return;
        boolean value = Codecs.BOOLEAN.decode(ops, input);
        bubbleColumn.setDrag(value);
    }
}
