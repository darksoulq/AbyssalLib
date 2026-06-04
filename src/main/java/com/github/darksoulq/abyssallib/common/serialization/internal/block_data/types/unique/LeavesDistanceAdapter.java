package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;

public class LeavesDistanceAdapter extends Adapter<Leaves> {

    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Leaves;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Leaves value) {
        return Codecs.INT.encode(ops, value.getDistance());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Leaves leaves))
            return DataResult.error(DataError.custom("Base is not Leaves, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            leaves.setDistance(value);
            return DataResult.success(null);
        });
    }
}