package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Snowable;

public class SnowableAdapter extends Adapter<Snowable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Snowable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Snowable value) {
        return Codecs.BOOLEAN.encode(ops, value.isSnowy());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Snowable snowable))
            return DataResult.error(DataError.custom("Base is not Snowable, got: " + base.getClass().getSimpleName()));
        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            snowable.setSnowy(value);
            return DataResult.success(null);
        });
    }
}