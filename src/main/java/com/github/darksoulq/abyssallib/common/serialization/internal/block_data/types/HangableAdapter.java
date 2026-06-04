package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Hangable;

public class HangableAdapter extends Adapter<Hangable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Hangable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Hangable value) {
        return Codecs.BOOLEAN.encode(ops, value.isHanging());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Hangable hangable))
            return DataResult.error(DataError.custom("Base is not Hangable, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            hangable.setHanging(value);
            return DataResult.success(null);
        });
    }
}