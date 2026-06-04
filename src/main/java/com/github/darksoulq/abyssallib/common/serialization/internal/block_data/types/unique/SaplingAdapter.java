package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Sapling value) {
        return Codecs.INT.encode(ops, value.getStage());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Sapling sapling))
            return DataResult.error(DataError.custom("Base is not Sapling, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value > sapling.getMaximumStage()) {
                return DataResult.error(DataError.outOfBounds(value, 0, sapling.getMaximumStage()));
            }
            sapling.setStage(value);
            return DataResult.success(null);
        });
    }
}