package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, BubbleColumn value) {
        return Codecs.BOOLEAN.encode(ops, value.isDrag());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof BubbleColumn bubbleColumn))
            return DataResult.error(DataError.custom("Base is not BubbleColumn, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            bubbleColumn.setDrag(value);
            return DataResult.success(null);
        });
    }
}