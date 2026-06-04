package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Brushable value) {
        return Codecs.INT.encode(ops, value.getDusted());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Brushable brushable))
            return DataResult.error(DataError.custom("Base is not Brushable, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value > brushable.getMaximumDusted()) {
                return DataResult.error(DataError.custom("Dusted value (" + value + ") exceeds maximum (" + brushable.getMaximumDusted() + ")"));
            }
            brushable.setDusted(value);
            return DataResult.success(null);
        });
    }
}