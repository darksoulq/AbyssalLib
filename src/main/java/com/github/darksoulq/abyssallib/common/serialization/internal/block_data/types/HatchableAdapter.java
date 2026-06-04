package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Hatchable;

public class HatchableAdapter extends Adapter<Hatchable> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof Hatchable;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Hatchable value) {
        return Codecs.INT.encode(ops, value.getHatch());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof Hatchable hatchable))
            return DataResult.error(DataError.custom("Base is not Hatchable, got: " + base.getClass().getSimpleName()));

        return Codecs.INT.decode(ops, input).flatMap(value -> {
            if (value > hatchable.getMaximumHatch()) {
                return DataResult.error(DataError.custom("Hatch value (" + value + ") exceeds maximum (" + hatchable.getMaximumHatch() + ")"));
            }
            hatchable.setHatch(value);
            return DataResult.success(null);
        });
    }
}