package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;

public class LevelledAdapter extends Adapter<Levelled> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Levelled;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Levelled value) {
        return Codecs.INT.encode(ops, value.getLevel());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Levelled levelled))
            return DataResult.error(DataError.custom("Base is not Levelled, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value > levelled.getMaximumLevel()) {
                return DataResult.error(DataError.custom("Level value (" + value + ") exceeds maximum (" + levelled.getMaximumLevel() + ")"));
            }
            levelled.setLevel(value);
            return DataResult.success(null);
        });
    }
}