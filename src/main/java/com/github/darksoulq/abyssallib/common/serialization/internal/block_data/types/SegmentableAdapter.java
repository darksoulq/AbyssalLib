package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Segmentable value) {
        return Codecs.INT.encode(ops, value.getSegmentAmount());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Segmentable segmentable))
            return DataResult.error(DataError.custom("Base is not Segmentable, got: " + base.getClass().getSimpleName()));
        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value < segmentable.getMinimumSegmentAmount() || value > segmentable.getMaximumSegmentAmount()) {
                return DataResult.error(DataError.custom("Segment amount (" + value + ") out of bounds (" + segmentable.getMinimumSegmentAmount() + "-" + segmentable.getMaximumSegmentAmount() + ")"));
            }
            segmentable.setSegmentAmount(value);
            return DataResult.success(null);
        });
    }
}